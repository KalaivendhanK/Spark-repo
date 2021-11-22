package com.home.sparkStreaming

import org.apache.spark.sql.SparkSession

object SimpleWordCount extends App {

  val spark = SparkSession.builder().master("local[*]").getOrCreate()

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "quickstart-events")
    .option("startingOffsets", "earliest") // From starting
    .load()

  df.writeStream
    .format("console")
    .outputMode("append")
    .start()
    .awaitTermination()

  spark.close()
}
