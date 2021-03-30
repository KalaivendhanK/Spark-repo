package sparkSpace

import org.apache.spark.sql.SparkSession

object AccessMeldHive {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
//      .enableHiveSupport()
      .getOrCreate()

    val data  = spark.read.csv("""C:\\Users\\kkalya622\\Desktop\\query_exports\\activation_step\\""")
    data.count()
    data.show()
    //    val driverName = "org.apache.hive.jdbc.HiveDriver"
    //    Class.forName(driverName)
    //    val df = spark.read
    //      .format("jdbc")
    //      .option("url", "jdbc:hive2://ebdp-ch2-d769p.sys.comcast.net:8443/dmr_dev?user=kkalya622;password=Comcastpass123;AuthMech=3&transportMode=http;httpPath=gateway/default/hive&SSL=1;SSLTrustStore=C:/Users/kkalya622/Downloads/Important/PROD_KNOX_D769P.jks;SSLTrustStorePwd=CoreKnoxMeldGateway;http.header.Connection=close")
    //      .option("dbtable","activation_step")
    //      .option("","")
    //      .option("","")
    //      .option("","")
    //      .option("","")
    //      .load()

    //    df.count()

  }
}
