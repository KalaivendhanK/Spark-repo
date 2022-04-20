package sparkTestSpace

import org.apache.spark.sql.{DataFrame, SparkSession}
//import org.apache.spark.sql.QueryTest
//import org.apache.spark.sql.test.SharedSQLContext
import zio.test._
import zio._
import zio.test.Assertion._

object MovingAverageZIOTest extends DefaultRunnableSpec {
  val spec1: Spec[Any, TestFailure[Nothing], TestSuccess] =
    suite("sampleSuite") {
      test("sampleTest") {
        assert(1)(equalTo(1))
      }
    }

  val spec2: Spec[Any, TestFailure[Nothing], TestSuccess] =
    suite("MovingAverageTest") {
      test("The test and actual dataframe values should match") {
        import zioSpark.zioServiceLayerImplementation2._
        case class SourceHandlerTest(spark: SparkSession) extends SourceHandler {
          override def readCSV(
              filePath: String
          ): ZIO[Any, Exception, DataFrame] =
            ZIO.succeed(spark.read.option("header", "true").csv(filePath))
        }

        object SourceHandlerTest {
          def layer: ZLayer[SparkSession, Nothing, SourceHandler] =
            (SourceHandlerTest.apply _).toLayer
        }

        object TestFilePath {
          def filePath: String = "mysqlSpark/src/test/resources/stocks/stock_prices.csv"
        }

        def actual: ZIO[Any, Nothing, Long] =
          finalImpl(TestFilePath.filePath)
            .provide(
              SparkLive.layer,
              SourceHandlerTest.layer,
              MovingAverageLive.layer
            )
            .tap(df ⇒ ZIO.succeed(println(df)))
            .flatMap(df ⇒ ZIO.succeed(df.count))

        def expected: ZIO[Any, Throwable, Long] = (for {
          spark ← ZIO.service[SparkSession]
          count ← ZIO
                    .succeed(spark.read.option("header", "true").csv(TestFilePath.filePath))
                    .tap(df ⇒ ZIO.succeed(println(df)))
                    .flatMap(df ⇒ ZIO.succeed(df.count))
        } yield count).provide(SparkLive.layer)

        assert(expected.orDie.exitCode)(equalTo(actual.exitCode))
      }
    }

  val finalTestSpec: Spec[Any, TestFailure[Nothing], TestSuccess] =
    suite("finalSuite")(spec1 + spec2)

  override def spec: ZSpec[TestEnvironment, Any] = finalTestSpec
}
