package sparkTestSpace

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.scalatest._
import sparkSpace.RunningTotal._

class SparkTest extends FunSuite with BeforeAndAfterEach {
  val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
  import spark.implicits._

  val aDataFrame: DataFrame = Seq(
    ("A1", "D1", 10000),
    ("A2", "D1", 8000),
    ("A3", "D1", 5000),
    ("A4", "D2", 6000),
    ("A5", "D2", 4000),
    ("A6", "D3", 5000),
    ("A7", "D3", 6000),
    ("A8", "D4", 5000),
  ).toDF("name", "dept", "sal")

  val result: DataFrame = queryData(spark, aDataFrame)

  test("test1") {
    assert(result.count() === 6)
  }

  test("Max count should not be greater than 43000") {
    val maxSalary: Array[Int] = result
      .agg(max(col("running_total")))
      .collect()
      .map {
        case x => x(0).toString.toInt
        case _ => 999999
      }
    assert(maxSalary(0) <= 43000)
  }

}
