package sparkSpace

import org.apache.spark.sql._
import functions._
import zio.{ ZIO, ZLayer, _ }

import scala.concurrent.duration.DurationInt

object DeltaLake extends App {

  val PARQUET_PATH = "mysqlSpark/src/main/output/delta_tutorial/parquet_table"
  val DELTA_SILVER_PATH = "mysqlSpark/src/main/output/delta_tutorial/delta_table"
  val DELTA_GOLD_PATH = "mysqlSpark/src/main/output/delta_tutorial/delta_agg_table"

  val spark = SparkSession.builder().master("local").getOrCreate()
  val sc = spark.sparkContext

  spark.sparkContext.setLogLevel("info")

  //  val raw_data: DataFrame = spark.range(100000)
  //    .selectExpr("if(id % 2 = 0, 'Open', 'Close') as action")
  //    .withColumn("date", expr("cast(concat('2019-04-', cast(rand(5) * 30 as int) + 1) as date)"))
  //    .withColumn("device_id", expr("cast(rand(5) * 100 as int)"))

  //  raw_data.write.format("parquet").partitionBy("date").save(PARQUET_PATH)
  //  raw_data.write.format("delta").partitionBy("date").save(DELTA_SILVER_PATH)

  //   val stream_data: DataFrame = spark.readStream.format("rate").option("rowsPerSecond", 100).load()
  //  .selectExpr("'Open' as action")
  //  .withColumn("date", expr("cast(concat('2019-04-', cast(rand(5) * 30 as int) + 1) as date)"))
  //  .withColumn("device_id", expr("cast(rand(5) * 500 as int)"))

  trait DataSources {
    def staticData: ZIO[SparkSession, Throwable, DataFrame]
    def dataStream: ZIO[SparkSession, Throwable, DataFrame]
  }

  val managedSparkSession: ZManaged[Any, Nothing, SparkSession] = ZManaged.make(ZIO.succeed(spark))(spark => ZIO.succeed(spark.close()))

  val sparkSessionLayer: ZLayer[Any, Nothing, Has[SparkSession]] = managedSparkSession.toLayer

  object DataSources {
    val testImpl: ZLayer[Has[SparkSession], Throwable, Has[DataSources]] = ZLayer.fromFunction { spark =>
      new DataSources {
        override def staticData: Task[DataFrame] = ZIO {
          spark.get.range(100000)
            .selectExpr("if(id % 2 = 0, 'Open', 'Close') as action")
            .withColumn("date", expr("cast(concat('2019-04-', cast(rand(5) * 30 as int) + 1) as date)"))
            .withColumn("device_id", expr("cast(rand(5) * 100 as int)"))
        }

        override def dataStream: Task[DataFrame] = ZIO {
          spark.get.readStream.format("rate").option("rowsPerSecond", 100).load()
            .selectExpr("'Open' as action")
            .withColumn("date", expr("cast(concat('2019-04-', cast(rand(5) * 30 as int) + 1) as date)"))
            .withColumn("device_id", expr("cast(rand(5) * 500 as int)"))
        }
      }
    }

    //    def staticData = ZIO.accessM[DataSources] { ds =>
    //      ds.staticData
    //    }
  }
  type HasDW = Has[DataWriter]
  trait DataWriter {
    def staticWrite(dataFrame: DataFrame): ZIO[Any, Throwable, Unit]
    def streamWrite(dataFrame: DataFrame): ZIO[Any, Throwable, Unit]
  }
  object DataWriter {
    val testImpl: ZLayer[Any, Nothing, Has[DataWriter]] = ZLayer.fromFunction { _ =>
      new DataWriter {
        override def staticWrite(dataFrame: DataFrame): ZIO[Any, Throwable, Unit] = ZIO {
          dataFrame.write.format("parquet").partitionBy("date").save(PARQUET_PATH)
        }

        override def streamWrite(dataFrame: DataFrame): ZIO[Any, Throwable, Unit] = ZIO {
          //          dataFrame.get.writeStream.format("parquet").partitionBy("date").outputMode("append")
          //          .trigger(processingTime='5 seconds').option('checkpointLocation', PARQUET_PATH + "/_chk").start(PARQUET_PATH)
          ???
        }
      }
    }
  }

  val sourceLayer: ZLayer[Any, Throwable, Has[DataSources]] = sparkSessionLayer >>> DataSources.testImpl
  //
  //  val program = for {
  //    ds <- ZIO.accessM[Has[DataSources]]
  //    dw <- ZIO.accessM[HasDW]
  //
  //  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = ???
}
