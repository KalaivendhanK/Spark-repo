package sparkSpace

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{ StringType, StructField, StructType }

class Schema {
}

object LocalSparkTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("spark-local-test")
      .master("local[*]")
      .getOrCreate()
    val sc = spark.sparkContext

    import spark.implicits._
    val someDF = Seq(
      (1, "bat", 23),
      (1, "bat", 45),
      (1, "bat", 60),
      (2, "bat", 23),
      (2, "bat", 45),
      (2, "bat", 60),
      (3, "bat", 23),
      (3, "bat", 45),
    ).toDF("id", "name", "values")
    someDF.createTempView("test_table")

    val agg_data = spark sql ("select id,collect_list(values) from test_table group by id")
    val reference_data = spark sql ("select *,count(*) over (partition by id order by values asc) from test_table")
    reference_data show

  }
}
