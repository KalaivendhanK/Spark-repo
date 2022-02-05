/**
 * This job need to be executed through spark-submit.
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
 *     `spark-submit --class com.home.sparkStreaming.SimpleWordCount --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.3 C:\Kalai\Learning\Testing\core-workspace\streaming\target\scala-2.12\streaming_2.12-0.0.1.jar`
 *   - Leave the terminal open
 *
 * Testing:
 *   - Go to the kafka container
 *   - Produce the events into kafka topic by using kafka-console-producer.
 *     `kafka-console-producer --topic quickstart-events --bootstrap-server localhost:9092`
 *   - Type any sample words.
 *   - Navigate to the spark job to see the outputs.
 */
package com.home.sparkStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{ col, window }
import org.apache.spark.sql.streaming.Trigger._

object SimpleWordCount extends App {

  val spark = SparkSession.builder().master("local[*]").config("spark.sql.shuffle.partitions", "4").getOrCreate()

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "quickstart-events")
    .option("startingOffsets", "latest")
    .load()

  val wordCountDf = df
    .withWatermark("timestamp", "10 seconds")
    //    .selectExpr("cast(value as string) as text", "timestamp")
    .groupBy(
      window(col("timestamp"), "5 seconds"), col("value"))
    .count()

  wordCountDf.writeStream
    .format("console")
    .outputMode("append")
    .trigger(ProcessingTime(10000))
    .start()
    .awaitTermination()

  spark.close()
}
