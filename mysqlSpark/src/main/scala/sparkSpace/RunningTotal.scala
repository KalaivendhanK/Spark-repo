package sparkSpace

import org.apache.spark.sql.{ DataFrame, SparkSession }

object RunningTotal {
  def main(array: Array[String]): Unit = {

  }
  def queryData(spark: SparkSession, df: DataFrame): DataFrame = {
    df.createOrReplaceTempView("test")

    spark.sql("""
      SELECT * FROM (
        SELECT *
        ,SUM(sal) OVER (
            ORDER BY dept ASC
            ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
            ) AS running_total
        FROM test
        )
        WHERE running_total < 43000
         ORDER BY dept,running_total aSC
      """)
  }

}
