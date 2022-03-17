package com.home.sparkStreaming

import org.apache.spark.sql.functions.col

object StreamWriteAndBatchReadTest extends App with CommonSparkSession {
  override val masterUrl: String = "local[2]"

  import spark.implicits._
  case class WordCount(words: String, count: String)

  spark.read.option("header", "true").csv("output\\streaming\\wordcount\\data\\").as[WordCount]
    .filter(ds => ds.words != null && ds.words != " ").cache()
    .selectExpr("words", "cast(count as int) as count")
    .groupBy("words").sum("count").show(100, false)
}