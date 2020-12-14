package sparkSpace

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StringType, StructField, StructType}


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
      (1, "bat", 23),
      (1, "bat", 45),
    ).toDF("id", "name", "values")
    someDF show

  }
}
