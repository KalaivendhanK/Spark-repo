package com.home.akkaStream

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, FanInShape2, FlowShape, Graph, SinkShape, SourceShape, UniformFanOutShape}
import zio._
import zio.ZLayer._
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.language.postfixOps

object AkkaStreamExample {

  implicit val materializer: ActorSystem = ActorSystem()

  val source: Source[Int, NotUsed] = Source(1 to 1000)
  val flow: Flow[Int, Int, NotUsed] = Flow[Int].map(_*2)
  val sink: Sink[Any, Future[Done]] = Sink.foreach(println)

  val graph: RunnableGraph[NotUsed] = source via flow to sink

  val specialGraph: Graph[ClosedShape.type, NotUsed] = GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>

    import akka.stream.scaladsl.GraphDSL.Implicits._

    val source: SourceShape[Int] = builder.add(Source(1 to 1000))
    val flow1: FlowShape[Int, Int] = builder.add(Flow[Int].map(_ + 1))
    val flow2: FlowShape[Int, Int] = builder.add(Flow[Int].map(_ * 10))
    val sink: SinkShape[(Int, Int)] = builder.add(Sink.foreach[(Int,Int)](println))

    val broadcast: UniformFanOutShape[Int, Int] = builder.add(Broadcast[Int](2))
    val zip: FanInShape2[Int, Int, (Int, Int)] = builder.add(Zip[Int,Int])

    source ~> broadcast

    broadcast.out(0) ~> flow1 ~> zip.in0
    broadcast.out(1) ~> flow2 ~> zip.in1

    zip.out ~> sink

    ClosedShape
  }
  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(specialGraph).run()
  }
}
