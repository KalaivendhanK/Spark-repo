package sparkSpace

import org.scalatest.FunSuite
import LocalSparkTest._

object SparkTest extends FunSuite with LocalSparkContext {
  test("Sample test") {
    val ss = sparkSession
    import ss.implicits._
    val someDF = Seq(
      ("A1", "D1", 10000),
      ("A2", "D1", 8000),
      ("A3", "D1", 5000),
      ("A4", "D2", 6000),
      ("A5", "D2", 4000),
      ("A6", "D3", 5000),
      ("A7", "D3", 6000),
      ("A8", "D4", 5000),
    ).toDF("name", "dept", "sal")

    val result = queryData(ss, someDF)
    assert(result == 8)
  }

}
