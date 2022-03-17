package zioSpark

import org.apache.spark.sql._
import zio._

object SparkWithZIO extends ZIOAppDefault {

  trait SparkHandler {
    def createSparkSession: ZIO[Any, Nothing, SparkSession]
    def closeSparkSession(sparkSession: SparkSession): ZIO[Any, Nothing, Unit]
  }

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

  trait MovingAverage  {
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

  def sparkHandlerImpl: ZIO[SourceHandler with SparkHandler with MovingAverage, Nothing, Unit] =
    for {
      sparkHandler  ← ZIO.service[SparkHandler]
      sourceHandler ← ZIO.service[SourceHandler]
      movingAverage ← ZIO.service[MovingAverage]
      spark         ← sparkHandler.createSparkSession
      csvDf         ← sourceHandler.readCSV(spark, Constants.filePath).orDie
      movingAvgDF   ← movingAverage.calculate(csvDf).orDie
      _             ← ZIO.succeed(movingAvgDF.show(100, false))
      _             ← sparkHandler.closeSparkSession(spark)
    } yield ()

  val layerImpl: ZIO[Any, Nothing, Unit] =
    sparkHandlerImpl.provideLayer(SparkHandler.live ++ SourceHandler.live ++ MovingAverage.live)

  override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any] =
    layerImpl.exitCode

}
