/** This job need to be executed through spark-submit.
  * Requirements:
  *   - Docker Desktop
  *
  * Pre-execution steps:
  *   - Start the docker demon. Run the docker desktop application from start menu in case of Windows machine
  *   - Launch Git Bash
  *   - Navigate to this project and execute `docker-compose up` command. This will start the kafka broker and zookeeper instances.
  *     NOTE: Kafka will listen to the port 9092 on localhost.
  *   - Enter into the Kafka broker container by executing  `docker exec -it <container_id> bash`
  *     NOTE: Container id can be fetched by executing the docker command `docker container ls`
  *   - Create the kafka topic : `kafka-topics --create --topic quickstart-events --bootstrap-server localhost:9092`
  *   - List the topic : `kafka-topics --describe --topic quickstart-events --bootstrap-server localhost:9092`
  *
  * Deployment Steps:
  *   - Open the powershell
  *   - Navigate to this project directory. Select this project from the list of projects
  *   - Package the files as Jar using `sbt package` command in powershell
  *   - Navigate to the spark directory - `C:\Spark\spark3.0.3\bin`
  *   - Execute the spark job:
  *     `spark-submit --class com.home.sparkStreaming.SparkStreamWordCount --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.3 C:\Kalai\Learning\Testing\core-workspace\streaming\target\scala-2.12\streaming_2.12-0.0.1.jar`
  *   - Leave the terminal open
  *
  * Testing:
  *   - Go to the kafka container
  *   - Produce the events into kafka topic by using kafka-console-producer.
  *     `kafka-console-producer --topic quickstart-events --bootstrap-server localhost:9092`
  *   - Type any sample words.
  *   - Navigate to the spark job to see the outputs.
  */

/** Observations
  *  - An additional empty file is getting created at the end of every batch.
  *  - When there is new data from input kafka topic, only the PREVIOUS batch is getting processed.
  *    The file as well has the preious batch.
  *  -
  */
package com.home.sparkStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, window}
import org.apache.spark.sql.streaming.Trigger._
import org.apache.spark.sql.functions._

object SparkStreamWordCount extends App {

  val spark = SparkSession
    .builder()
    .master("local[4]")
    .config("spark.sql.shuffle.partitions", "1")
    .config("spark.dynamicAllocation.enabled", true)
    .config("spark.dynamicAllocation.shuffleTracking.enabled", true)
    .getOrCreate()

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "quickstart-events")
    .option("startingOffsets", "earliest")
    .load()

  val wordCountDf = df
    .withWatermark("timestamp", "10 seconds")
    .selectExpr("cast(trim(value) as string) as value", "timestamp")
    .filter(trim(col("value")).isNotNull && trim(col("value")) != " " && trim(col("value")) != null)
    .select(split(col("value"), " ").alias("arr_splits"), col("timestamp"))
    .select(explode(col("arr_splits")).alias("words"), col("timestamp"))
    .groupBy(window(col("timestamp"), "5 seconds"), col("words"))
    .count()
    .alias("count")
    .select(col("words"), col("count"))

  wordCountDf.writeStream
    .format("csv")
    .outputMode("append")
    .option("header", "true")
    .trigger(ProcessingTime(30 * 1000))
    .option("checkpointLocation", "output/streaming/wordcount/checkpointLocation/")
    .option("path", "output/streaming/wordcount/data/")
    .start()
    .awaitTermination()

  spark.close()
}
