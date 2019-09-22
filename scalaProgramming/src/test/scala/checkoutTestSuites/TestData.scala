package com.home.testing

import org.apache.spark.sql._
// import sql._
// import types._

final case class Schema(table_name: String, no_of_columns: Int, domain: String)

final private[testing] class TestData {
  val sc = SparkSession.builder().master("local").getOrCreate
  import sc.implicits._
  // val schema = StructType(
  //       StructField("TABLE_NAME", StringType, true) ::
  //       StructField("NO_OF_COLUMNS", IntegerType, false) ::
  //       StructField("DOMAIN", StringType, false) :: Nil
  //       )
  val testData = Seq(Schema("UserProfile", 10, "NOW"), Schema("UserProfile", 10, "GO"))
  val testDS = testData.toDS
  // val testDF = sc.createDataFrame(testData,schema)
}

private[testing] object TestData {
  def apply(): TestData = new TestData
}