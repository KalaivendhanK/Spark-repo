package sparkSpace

import org.apache.spark.sql.SparkSession

object AccessMeldHive {
  def main(args: Array[String]): Unit = {
      val spark = SparkSession
        .builder()
        .master("local")
        .config("hive.metastore.uris","")
        .enableHiveSupport()
        .getOrCreate()
  }
}
