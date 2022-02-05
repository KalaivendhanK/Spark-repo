package sparkSpace

import org.apache.spark.sql.{ Row, SparkSession }
import org.apache.spark.sql.functions.{ col, count, explode, split }

object WordCount extends App {
  val spark = SparkSession.builder.master("local").getOrCreate()
  val sc = spark.sparkContext

  import spark.implicits._

  val input_file = spark.read.text("C:\\Kalai\\Learning\\Testing\\core-workspace\\mysqlSpark\\src\\main\\resources\\wordCount\\wordcount.txt").cache()

  /**
    * Below is the rdd implementation of flatmap.
    * Note: when the Dataframe is converted into RDD.It gives us the RDD[Row]
    * object.Each row can be accessed using the name `value`
    */
  input_file.rdd
    .flatMap(x => x.toString().split(' '))
    .map(x => (x, 1))
    .reduceByKey((x, y) => x + y)
    .toDF("word", "counts")
    .orderBy(col("word"))
  //    .show()

  input_file.select(col("value"))
    .select(split(col("value"), " ").alias("split_values"))
    .select(explode(col("split_values")).alias("words"))
    .groupBy(col("words"))
    .agg(count(col("words")).alias("counts"))
    .orderBy(col("words"))
  //    .show()

  input_file.createOrReplaceTempView("input_view")
  spark.sql(
    """
      |select words,count(*) from (
      |select explode(split(value, " ")) as words from input_view
      |)a group by words order by words desc
      |""".stripMargin
  )
  //    .show()

  case class Lines(value: String)
  input_file.as[Lines]
    .flatMap(x => x.value.split(" "))
    .map(words => (words, 1))
    .toDF("word", "count")
    .groupBy(col("word"))
    .count()
  //    .show()
  val bank_info_input = spark.read
    .option("header", "true")
    .csv("C:\\Kalai\\Learning\\Testing\\core-workspace\\mysqlSpark\\src\\main\\resources\\wordCount\\bank_info.csv").cache()

  case class BankInfo(name: String, accountNumber: Int, balance: Int)
  val bankInfo = bank_info_input
  bankInfo
    .selectExpr("name", "cast(accnt_no as int) as accountNumber", "cast(balance as int) as balance")
    .as[BankInfo]
    .filter(bankInfo => bankInfo.name.toLowerCase.contains("k"))
    .map(bankInfo => (bankInfo.name, bankInfo.accountNumber, bankInfo.balance + 100))
    .show()

  spark.close()
}
