package zioSpark

import org.apache.spark.sql._
import zio._

/** ZIO 2.0 Service layer implementation
  * Module Pattern - 1
  */
object SparkWithZIO extends ZIOAppDefault {

  trait SparkHandler {
    def createSparkSession: ZIO[Any, Nothing, SparkSession]
    def closeSparkSession(sparkSession: SparkSession): ZIO[Any, Nothing, Unit]
  }

  val sparkLayer: ZLayer[Any, Nothing, SparkSession] = ZManaged.acquireReleaseWith {
    ZIO.succeed(SparkSession.builder().master("local").getOrCreate())
  } { spark ⇒
    ZIO.succeed(spark.close())
  }.toLayer

  object SparkHandler {
    val live: ZLayer[Any, Nothing, SparkHandler] = ZLayer.succeed {
      new SparkHandler {
        override def createSparkSession: ZIO[Any, Nothing, SparkSession] =
          ZIO.succeed(SparkSession.builder().master("local").getOrCreate())

        override def closeSparkSession(sparkSession: SparkSession): ZIO[Any, Nothing, Unit] =
          ZIO.succeed(sparkSession.close)
      }
    }
  }

  trait SourceHandler {
    def readCSV(sparkSession: SparkSession, filePath: String): ZIO[Any, Exception, DataFrame]
  }

  object SourceHandler {
    val live: ULayer[SourceHandler] = ZLayer.succeed {
      new SourceHandler {
        override def readCSV(
            spark: SparkSession,
            filePath: String
        ): ZIO[Any, Exception, DataFrame] =
          ZIO.succeed(spark.read.option("header", "true").csv(filePath))
      }
    }
  }

  trait MovingAverage {
    def calculate(df: DataFrame): ZIO[Any, Exception, DataFrame]
  }

  object MovingAverage {
    val live: ULayer[MovingAverage] = ZLayer.succeed {
      new MovingAverage {
        override def calculate(df: DataFrame): ZIO[Any, Exception, DataFrame] =
          ZIO.succeed(df.select("stockId", "stockPrice", "timeStamp"))
      }
    }
  }

  object Constants {
    val filePath = "mysqlSpark/src/main/resources/stocks/stock_prices.csv"
  }

  def sparkHandlerImpl: ZIO[SparkSession & SourceHandler & MovingAverage, Nothing, Unit] =
    for {
      //      sparkHandler  ← ZIO.service[SparkHandler]
      spark         ← ZIO.service[SparkSession]
      sourceHandler ← ZIO.service[SourceHandler]
      movingAverage ← ZIO.service[MovingAverage]
      //      spark         ← sparkHandler.createSparkSession
      csvDf         ← sourceHandler.readCSV(spark, Constants.filePath).orDie
      movingAvgDF   ← movingAverage.calculate(csvDf).orDie
      _             ← ZIO.succeed(movingAvgDF.show(100, false))
      //      _             ← sparkHandler.closeSparkSession(spark)
    } yield ()

  val layerImpl: ZIO[Any, Nothing, Unit] =
    sparkHandlerImpl.provideLayer(
      //      SparkHandler.live
      sparkLayer
        ++ SourceHandler.live
        ++ MovingAverage.live
    )

  override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any] =
    layerImpl.exitCode

}

/** ZIO 2.0 Service layer implementation
  * Module Pattern - 2
  */
object zioServiceLayerImplementation2 extends ZIOAppDefault {

  /** Spark Service
    */
  object SparkLive {
    val layer: ZLayer[Any, Nothing, SparkSession] = ZManaged.acquireReleaseWith {
      ZIO.succeed(SparkSession.builder().master("local").getOrCreate())
    } { spark ⇒
      ZIO.succeed(spark.close())
    }.toLayer
  }

  /** Spark Service
    *  trait SparkHandler {
    *    def createSparkSession: ZIO[Any, Nothing, SparkSession]
    *    def closeSparkSession(sparkSession: SparkSession): ZIO[Any, Nothing, Unit]
    *  }
    *
    *   object SparkHandler {
    *    def createSparkSession: ZIO[SparkHandler, Nothing, SparkSession] =
    *      ZIO.serviceWithZIO[SparkHandler](_.createSparkSession)
    *
    *    def closeSparkSession(sparkSession: SparkSession): ZIO[SparkHandler, Nothing, Unit] =
    *      ZIO.serviceWithZIO[SparkHandler](_.closeSparkSession(sparkSession))
    *  }
    *
    *  case class SparkHandlerLive() extends SparkHandler {
    *    override def createSparkSession: ZIO[Any, Nothing, SparkSession] =
    *      ZIO.succeed(SparkSession.builder().master("local").getOrCreate())
    *
    *    override def closeSparkSession(sparkSession: SparkSession): ZIO[Any, Nothing, Unit] =
    *      ZIO.succeed(sparkSession.close)
    *  }
    *
    *  object SparkHandlerLive {
    *    val layer: URLayer[Any, SparkHandlerLive] = (SparkHandlerLive.apply _).toLayer
    *  }
    */

  /** File Read Service
    */
  trait SourceHandler {
    def readCSV(filePath: String): ZIO[Any, Exception, DataFrame]
  }

  object SourceHandler {
    def readCSV(
        filePath: String
    ): ZIO[SourceHandler, Exception, DataFrame] =
      ZIO.serviceWithZIO[SourceHandler](_.readCSV(filePath))
  }

  case class SourceHandlerLive(spark: SparkSession) extends SourceHandler {
    override def readCSV(
        filePath: String
    ): ZIO[Any, Exception, DataFrame] =
      ZIO.succeed(spark.read.option("header", "true").csv(filePath))
  }

  object SourceHandlerLive {
    val layer: URLayer[SparkSession, SourceHandlerLive] = (SourceHandlerLive.apply _).toLayer
  }

  /** MovingAverage Service
    */
  trait MovingAverage {
    def calculate(df: DataFrame): ZIO[Any, Exception, DataFrame]
  }

  object MovingAverage {
    def calculate(df: DataFrame): ZIO[MovingAverage, Exception, DataFrame] =
      ZIO.serviceWithZIO[MovingAverage](_.calculate(df))
  }

  case class MovingAverageLive() extends MovingAverage {
    override def calculate(df: DataFrame): ZIO[Any, Exception, DataFrame] =
      ZIO.succeed(df.select("stockId", "stockPrice", "timeStamp"))
  }

  object MovingAverageLive {
    val layer: URLayer[Any, MovingAverageLive] = (MovingAverageLive.apply _).toLayer
  }

  object Constants {
    def filePath = "mysqlSpark/src/main/resources/stocks/stock_prices.csv"
  }

  def finalImpl(filePath: String): ZIO[SourceHandler & MovingAverage, Nothing, DataFrame] =
    for {
      csvDf       ← SourceHandler.readCSV(filePath).orDie
      movingAvgDF ← MovingAverage.calculate(csvDf).orDie
      _           ← ZIO.succeed(movingAvgDF.show(100, false))
    } yield movingAvgDF

  def layerImpl: ZIO[Any, Nothing, ExitCode] =
    finalImpl(Constants.filePath)
      .provide(
        //      SparkHandlerLive.layer,
        SparkLive.layer,
        SourceHandlerLive.layer,
        MovingAverageLive.layer
      )
      .exitCode

  override def run = layerImpl
}
