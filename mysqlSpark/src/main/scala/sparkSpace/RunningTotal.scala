package sparkSpace

import org.apache.spark.sql.{ DataFrame, SparkSession }

object RunningTotal {
  def main(array: Array[String]): Unit = {

  }
  def queryData(spark: SparkSession, df: DataFrame): DataFrame = {
    df.createOrReplaceTempView("test")

    val reference_data = spark sql ("""
      select * from (
        select *
        ,sum(sal) over (order by dept asc ROWS BETWEEN unbounded preceding AND CURRENT ROW) as running_total
        from test
        )
        where running_total < 43000
         order by dept,running_total asc
      """)
    reference_data
  }

}
