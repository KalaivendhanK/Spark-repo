
Mysql with Spark:
================
Pre requisites:
---------------
1. Sbt installed 
    Steps to install sbt
    - curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
    - sudo yum install sbt

2. Include the mysql connector in library dependency
    - libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.16"

3. Download mysql driver from maven repo using below command
    - wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.11/mysql-connector-java-8.0.11.jar


Steps to install mysql:
--------------------------


    #Install mysql-server in the server machine
    - sudo yum install mysql-server

    #Check the installed mysql server
    - rpm -qa mysql55-server

    #verify the init scripts for mysql
    - file /etc/init.d/mysqld

    #Start the mysql
    - sudo /etc/init.d/mysqld start
        This will start the mysql server on default port 3306

    #Download and extract sample mysql database file from internet
    - wget http://www.mysqltutorial.org/wp-content/uploads/2018/03/mysqlsampledatabase.zip
    - unzip mysqlsampledatabase.zip

Login into mysql cli
--------------------
    #Execute below to enter into cli
    - mysql -u root -p 

    #Create database and tables using the sample file downloaded from internet
    - source mysqlsampledatabase.zip
        This will create the customers relational database and tables.


Accessing mysql tables using Spark:
-----------------------------------
# Clone the repo and run the below commands to exeute the spark job that access mysql tables.

#Create a scala file with below code in the src/main/scala directory.

import org.apache.spark.sql.SparkSession


object AccessingMysqlFromSpark {

case class CustomerSchema(
customerNumber:Int, customerName:String, contactLastName:String, contactFirstName:String,
phone:String, addressLine1:String, addressLine2:String, city:String, state:String,
postalCode:String, country:String, salesRepEmployeeNumber:Int
)

def main(args:Array[String]): Unit={

val spark = SparkSession.builder().master("local[4]").getOrCreate
import spark.implicits._

val mysqlCustomersDF = spark.read.format("jdbc").
option("url", "jdbc:mysql://localhost:3306/classicmodels").
option("driver", "com.mysql.jdbc.Driver").
option("dbtable", "customers").
option("user", "root").
option("password", "mysql").
load()

val customersDS = mysqlCustomersDF.as[CustomerSchema]

println(s"""|some text
            |line 1
            |line 2
""".stripMargin)

customersDS.createOrReplaceTempView("customers")

val totalCustomersCount = spark.sql("select count(*) from customers")

totalCustomersCount.show

}
}


#Run sbt package to create a jar and run it using spark-submit command

Example:
    - sbt package
    - spark-submit --class AccessingMysqlFromSpark mysql-spark_2.12-0.0.1-SNAPSHOT.jar
