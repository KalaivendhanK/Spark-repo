package sparkSpace

import org.apache.spark.sql.SparkSession

object AccessingMysqlFromSpark {

  case class CustomerSchema(
      customerNumber: Int, customerName: String, contactLastName: String, contactFirstName: String,
      phone: String, addressLine1: String, addressLine2: String, city: String, state: String,
      postalCode: String, country: String, salesRepEmployeeNumber: Int
  )

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local[4]").getOrCreate
    import spark.implicits._

    val mysqlCustomersDF = spark.read.format("jdbc").
      option("url", "jdbc:mysql://localhost:3306/mysql").
      option("driver", "com.mysql.jdbc.Driver").
      option("dbtable", "help_topic").
      option("user", "root").
      option("password", "Comcastpass123").
      load()

    //    val customersDS = mysqlCustomersDF.as[CustomerSchema]
    //    customersDS.createOrReplaceTempView("customers")
    println(s"""|some text
            |line 1
            |line 2
""".stripMargin)

    mysqlCustomersDF.createOrReplaceTempView("customers")

    val totalCustomersCount = spark.sql("select count(*) from customers")

    totalCustomersCount.show

  }
}
