package com.home.kafkaStreams

import org.apache.kafka.clients.consumer.ConsumerConfig

import java.time.Duration
import java.util.Properties
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.serialization.Serdes.{ longSerde, stringSerde }
import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig }

object KafkaWorkCount extends App {

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-kafla-streams")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }

  val builder: StreamsBuilder = new StreamsBuilder

  val textLines: KStream[String, String] = builder.stream[String, String]("quickstart-events")

  def peekFunction[A, B, C](msg: String, a: A, b: B): Unit = println(s"$msg: $a, $b")

  val wordCounts: KTable[String, Long] = textLines.peek((a, b) => peekFunction("First Step", a, b))
    .flatMapValues(textLine => textLine.toLowerCase.split("\\W+"))
    .peek((a, b) => peekFunction("After flatmap Step", a, b))
    .groupBy((_, word) => word)
    .count()(Materialized.as("counts-store"))

  //  wordCounts.toStream.to("WordsWithCounts-kafka-streams")
  wordCounts.toStream.peek((a, b) => peekFunction("Final Output", a, b))

  //  wordCounts.filter { case (k, v) => k == "test" }.toStream.to("TestCounts")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10))
  }

}
