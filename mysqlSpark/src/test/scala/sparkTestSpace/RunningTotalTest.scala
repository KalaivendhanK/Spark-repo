package sparkTestSpace

import org.apache.spark.sql._
import org.apache.spark.sql.QueryTest
import org.apache.spark.sql.test.SharedSQLContext
import sparkSpace.RunningTotal._

class RunningTotalTest extends QueryTest with SharedSQLContext {

  test("test1") {
    spark.sparkContext.setLogLevel("ERROR")

    val input: DataFrame = spark.createDataFrame(Seq(
      ("A1", "D1", 10000),
      ("A2", "D1", 8000),
      ("A3", "D1", 5000),
      ("A4", "D2", 6000),
      ("A5", "D2", 4000),
      ("A6", "D3", 5000),
      ("A7", "D3", 6000),
      ("A8", "D4", 5000),
    ))

    val expected: DataFrame = spark.createDataFrame(Seq(
      ("A1", "D1", 10000),
      ("A2", "D1", 8000),
      ("A3", "D1", 5000),
      ("A4", "D2", 6000),
      ("A5", "D2", 4000),
      ("A6", "D3", 5000),
      ("A7", "D3", 6000),
      ("A8", "D4", 5000),
    ))

    //    lazy val result: DataFrame = queryData(spark, input)
    checkAnswer(input, expected)
  }

  //  test("Max count should not be greater than 43000") {
  //    val maxSalary: Array[Int] = result
  //      .agg(max(col("running_total")))
  //      .collect()
  //      .map {
  //        case x => x(0).toString.toInt
  //        case _ => 999999
  //      }
  //    checkAnswer(maxSalary(0) <= 43000)
  //  }

}
