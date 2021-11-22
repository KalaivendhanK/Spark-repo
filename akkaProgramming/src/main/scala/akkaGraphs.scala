import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, FanInShape2, FlowShape, Graph, SinkShape, SourceShape, UniformFanOutShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source, Zip}

import scala.concurrent.Future
import scala.util.{Failure, Success}


object AkkaStreamExample extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 1000)
  val flow:   Flow[Int, Int, NotUsed] = Flow[Int].map(_ * 2)
  val sink:   Sink[Any, Future[Done]] = Sink.foreach(println)

  val graph: RunnableGraph[NotUsed] = source via flow to sink

  val specialGraph: Graph[ClosedShape.type, NotUsed] = GraphDSL.create() {
    implicit builder: GraphDSL.Builder[NotUsed] =>
      import akka.stream.scaladsl.GraphDSL.Implicits._

      val source: SourceShape[Int] = builder.add(Source(1 to 1000))
      val flow1:  FlowShape[Int, Int] = builder.add(Flow[Int].map(_ + 1))
      val flow2:  FlowShape[Int, Int] = builder.add(Flow[Int].map(_ * 10))
      val sink:   SinkShape[(Int, Int)] = builder.add(Sink.foreach[(Int, Int)](println))

      val broadcast: UniformFanOutShape[Int, Int] = builder.add(Broadcast[Int](2))
      val zip:       FanInShape2[Int, Int, (Int, Int)] = builder.add(Zip[Int, Int])

      source ~> broadcast

      broadcast.out(0) ~> flow1 ~> zip.in0
      broadcast.out(1) ~> flow2 ~> zip.in1

      zip.out ~> sink

      ClosedShape
  }
  RunnableGraph.fromGraph(specialGraph).run()
}

object akkaGraphsBasic extends App{

  implicit val system: ActorSystem = ActorSystem("akkaGraphsBasic")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val flow: Flow[Int, Int, NotUsed] = Flow[Int].map(x => x + 10)
  val foldSink: Sink[Int, Future[Int]] = Sink.fold[Int,Int](0)(_ + _)
  val printSink = Sink.foreach(println)
  val sourceGraph = Source[Int](1 to 100).viaMat(flow)(Keep.right).toMat(foldSink)((sourceMat,sinkMat) => sinkMat)
  sourceGraph.run().onComplete{ value => println(s"sum: $value") }

}

object akkaGraphsAdv extends App {
  implicit val system: ActorSystem = ActorSystem("akkaGraphsAdv")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val source = Source(1 to 10)
  val multiplicationFlow = Flow[Int].map(_ * 2)
  val additionFlow = Flow[Int].map(_ + 2)
  val sink = Sink.foreach(println)
  val sinkFold = Sink.fold[Int,Int](0)(_ + _)
  val sinkFoldTuple = Sink.fold[(Int,Int),(Int,Int)]((0,0)){(a,b) =>
    val tuple = (a._1 + b._1 , a._2 + b._2)
    println(tuple)
    tuple
  }

  val graph: Graph[ClosedShape, NotUsed] = GraphDSL.create(){ implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

//    val source = builder.add(Source(1 to 10))
    val broadcast = builder.add(Broadcast[Int](2))
    val zip = builder.add(Zip[Int,Int])

    source ~>  broadcast
    broadcast.out(0) ~> multiplicationFlow ~> zip.in0
    broadcast.out(1) ~> additionFlow ~> zip.in1
    zip.out ~> sinkFoldTuple
//    broadcast.out(0) ~> multiplicationFlow ~> sinkFold
//    broadcast.out(1) ~> additionFlow ~> sink

    ClosedShape
  }

  RunnableGraph.fromGraph(graph).run()

}

object MaterializedGraphs extends App {

  implicit val system: ActorSystem = ActorSystem("akkaGraphsAdv")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val simpleSource = Source(1 to 10)
  val simpleFlow1 = Flow[Int].map(_ * 2)
  val simpleFlow2 = Flow[Int].map(_ * 2)
  val simpleSink1 = Sink.foreach[Int](x => println(s"Job 1: ${x}"))
  val simpleSink2 = Sink.fold[Int,Int](0)(_ + _)

  /**
    * Create a separate sink Graph which can then be used as a Sink Component later for some other flows
    */
  val sinkGraph = GraphDSL.create(simpleSink1,simpleSink2)(Keep.right){ implicit builder => (sink1Shape , sink2Shape) =>
    import GraphDSL.Implicits._

    val broadcast = builder.add(Broadcast[Int](2))

    broadcast ~> simpleFlow1 ~> sink1Shape
    broadcast ~> simpleFlow2 ~> sink2Shape

    SinkShape(broadcast.in)
  }

  /**
    * Use the sink component created above and connect it to some other source.
    * This is job 1 trigger
    */
  val resultFuture  = simpleSource.toMat(Sink.fromGraph(sinkGraph))(Keep.right).run()
  resultFuture.onComplete{
    case Success(value) => println(s"The sum is ${value}")
    case Failure(ex) => println(s"Exception: ${ex}")
  }

  /**
    * separate Flow Graph which can later be used as flow component
    */
  def enhancedFlow[A,B](flow: Flow[A,B,_]): Flow[A,B,Future[Int]] = {
    val counterSink = Sink.fold[Int, B](0)((a,_) => a +1)
   Flow.fromGraph{
    GraphDSL.create(counterSink) { implicit builder => counterSinkShape =>
      import GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[B](2))
      val aFlow = builder.add(flow)

      aFlow ~> broadcast ~> counterSinkShape
      FlowShape(aFlow.in, broadcast.out(1))
    }
   }
  }

  val aGraph = RunnableGraph.fromGraph( GraphDSL.create(){ implicit builder =>
    import GraphDSL.Implicits._

    val aSource = Source(1 to 50)
    val aSimpleFlow = Flow[Int].map(_ + 2)

    val aSimpleSink = builder.add(Sink.foreach[Int]{x => println(s"Job 2: ${x}") })

    aSource ~> builder.add(aSimpleFlow) ~> aSimpleSink
    ClosedShape
  })

  /**
    * This is job 2 trigger. Both Job 1 and Job 2 Graphs execute parallely
    */
    aGraph.run()
}