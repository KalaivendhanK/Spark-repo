/**
 * This program is a follow up practice from the youtube video by RockTheJVM
 * YouTube Video URL - https://www.youtube.com/watch?v=ExFjjczwwHs
 * Pre-requisites:
 *   Execute the docker-compose.yml file using `docker-compose up` command
 *   This will create the zookeeper and kafka containers running in the background at localhost:9092
  *
  *   There are two separate applications in this example program 1. Consumer , 2. Producer
  *   Execute the consumer first followed by producer
 */
package com.home.zioKafka

import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.json._
import zio.kafka.consumer._
import zio.kafka.producer._
import zio.kafka.serde.Serde
import zio.stream._

object ZioKafka extends zio.App {

  /**
   * Create consumer settings to read from the local host
   */
  val consumerSettings: ConsumerSettings =
    ConsumerSettings(List("localhost:9092"))
      .withGroupId("updates-consumer")

  val managedConsumer: RManaged[Clock with Blocking, Consumer.Service] =
    Consumer
      .make(consumerSettings)

  val consumer: ZLayer[Clock with Blocking, Throwable, Has[Consumer.Service]] =
    ZLayer
      .fromManaged(managedConsumer)
  /**
   * Read the stream of records from the kafka topic
   */
  //  val footballMatchesStream: ZStream[Any with Consumer, Throwable, CommittableRecord[String, String]] =
  //    Consumer
  //      .subscribeAnd(Subscription.topics("updates"))
  //      .plainStream(Serde.string,Serde.string)

  case class MatchPlayer(name: String, score: Int) {
    override def toString: String = s"$name - $score"
  }

  /**
   * Encoder and decoder for MatchPlayer type.
   * ZIO's gen method uses macros in the backend to encode and decode the MatchPlayer type
   */
  object MatchPlayer {
    implicit val encoder: JsonEncoder[MatchPlayer] =
      DeriveJsonEncoder.gen[MatchPlayer]

    implicit val decoder: JsonDecoder[MatchPlayer] =
      DeriveJsonDecoder.gen[MatchPlayer]
  }

  /**
   * Encoder and decoder for Match type.
   * ZIO's gen method uses macros in the backend to encode and decode the Match type
   */
  case class Match(players: Array[MatchPlayer]) {
    def score: String = s"${players(0)} - ${players(1)}"
  }
  object Match {
    implicit val encoder: JsonEncoder[Match] =
      DeriveJsonEncoder.gen[Match]

    implicit val decoder: JsonDecoder[Match] =
      DeriveJsonDecoder.gen[Match]
  }

  /**
   * Convert the `string of json` into `Match` type and vice-verca
   */
  val matchSerde: Serde[Any, Match] =
    Serde
      .string
      .inmapM( (string : String) =>
        ZIO
          .fromEither( string.fromJson[Match]
          .left
          .map(errorMessage => new RuntimeException(errorMessage)))
    ) { (theMatch: Match) =>
      ZIO
        .effect(theMatch.toJson)
    }

  /**
   * create stream with serde to encode and decode `Match` data types
   */
  val matchesStream: ZStream[Any with Consumer, Throwable, CommittableRecord[String, Match]] =
    Consumer
      .subscribeAnd(Subscription.topics("updates"))
      .plainStream(Serde.string, matchSerde)

  /**
    * Transform and print the stream values.
    * Also, keep track of the last processed values of the stream by using `offsetBatches` function.
    */
  val matchesPrintableStream: ZStream[Console with Any with Consumer with Clock, Throwable, OffsetBatch] =
    matchesStream
      .map(cr => (cr.value.score, cr.offset))
      .tap { case (score, _) => zio.console.putStrLn(s"| $score |") }
      .map(_._2)
      .aggregateAsync(Consumer.offsetBatches)

  /**
    * Run's the stream into the Sink.
    */
  val streamEffect: ZIO[Console with Any with Consumer with Clock, Throwable, Unit] =
    matchesPrintableStream
      .run(ZSink.foreach(offset => offset.commit))
      .tap( _ => zio.console.putStrLn("Waiting for input events"))

  /**
    * Main program to process the values in kafka stream.
    * The effect is injected with the dependencies `consumer` and `console` and ran at the END OF THE WORLD
    */
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    streamEffect
      .provideSomeLayer(consumer ++ zio.console.Console.live)
      .exitCode
}

/**
  * Simple program to generated hardcoded data into the kafka topic
  * Below produces sample data to the kafka topic to be read by the consumer program above
  */
object ZioKafkaProducer extends zio.App{
  import ZioKafka._
  val producerSettings: ProducerSettings =
    ProducerSettings(List("localhost:9092"))

  val producerManaged =
    Producer
      .make(producerSettings, Serde.string, matchSerde)

  val producer: ZLayer[Blocking, Throwable, Producer[Any, String, Match]] =
    ZLayer
      .fromManaged(producerManaged)

  val finalScore: Match = Match(Array(
    MatchPlayer("ITA",0),
    MatchPlayer("ENG",1)
  ))

  val producerRecord: ProducerRecord[String, Match] = new ProducerRecord[String, Match]("updates", "update-3", finalScore)

  val producerEffect: RIO[Producer[Any, String, Match], RecordMetadata] = Producer.produce(producerRecord)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    producerEffect.provideSomeLayer(producer).exitCode
  }
}