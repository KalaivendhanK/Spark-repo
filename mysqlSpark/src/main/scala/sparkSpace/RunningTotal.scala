package sparkSpace

import org.apache.spark.sql.{DataFrame, SparkSession}

object RunningTotal {

  def main(array: Array[String]): Unit = {
    val spark: SparkSession   = SparkSession.builder().master("local").getOrCreate()
    import spark.implicits._
    val aDataFrame: DataFrame = Seq(
      ("A1", "D1", 10000),
      ("A2", "D1", 8000),
      ("A3", "D1", 5000),
      ("A4", "D2", 6000),
      ("A5", "D2", 4000),
      ("A6", "D3", 5000),
      ("A7", "D3", 6000),
      ("A8", "D4", 5000)
    ).toDF("name", "dept", "sal")

    val result: DataFrame = queryData(spark, aDataFrame)
    result.show(100, false)

    spark.close()
  }
  def queryData(spark: SparkSession, df: DataFrame): DataFrame = {
    df.createOrReplaceTempView("test")

    //    spark.sql("""
    //      SELECT *,
    //      case when running_total >= 43000 and lag(running_total) over (order by running_Total)  + lead(sal) over(order by running_total asc)  <= 43000 then 1 else 0 end as new_rt
    //      --,running_total +lead(sal) over(order by running_total asc)
    //      ,lag(running_total) over (order by running_Total)  + lead(sal) over(order by running_total asc)
    //      FROM (
    //        SELECT *
    //        ,SUM(sal) OVER (
    //            ORDER BY dept ASC
    //            ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    //            ) AS running_total
    //        FROM test
    //        )a
    //        --WHERE running_total <= 43000 --or new_rt = 1
    //         ORDER BY dept,running_total aSC
    //
    //      """)
    //    TODO: Need to work on the solution for this question
    spark.sql(
      """
        |with ds1 as (
        |
        |
        |)
        |run_Total_max as (
        | select cast(43000 - max_run_total as int) as difference_sum from (
        |   select max(running_total) max_run_total from ds1 where ds1.running_total <= 43000
        | )
        |)
        |select a.*,b.* from ds1 a left join
        |ds1 b on a.running_Total > 43000 and b.running_total > 43000
        |--and a.running_Total - b.sal <=  38000
        |""".stripMargin
    )
  }

}
