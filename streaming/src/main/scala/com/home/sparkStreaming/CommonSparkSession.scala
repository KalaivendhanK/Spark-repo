package com.home.sparkStreaming

import org.apache.spark.sql.SparkSession

trait CommonSparkSession {
  val masterUrl: String
  final lazy val spark: SparkSession = SparkSession.builder().master(masterUrl).getOrCreate()
  final lazy val sc = spark.sparkContext
}
