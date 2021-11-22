import zio.ZIOAspect.debug.@@
import zio._
import zio.ZIOAspect._

object anObject extends ZIOAppDefault {
  val countErrors = ZIOMetric.countErrors("my service errors")

  val aList = List(1,2,3)

  def aFunction(list: List[Int]): ZIO[Has[Console], Throwable, Unit] =
     ZIO.foreach(list) { elem =>
       Console.printLine(s"Elements: ${elem}")
     } *> ZIO.succeed()

  val finalCode: ZIO[Any, Throwable, Unit] = aFunction(aList).inject(Console.live) @@ countErrors

  override def run: ZIO[ZEnv with Has[ZIOAppArgs], Any, Any] = finalCode.exitCode
}
//object zioPlayground extends zio.ZIOApp {
//  override implicit def tag: zio.Tag[zioPlayground.type] = ???
//
//  override type Environment = this.type
//
//  override def serviceBuilder: ZServiceBuilder[Has[ZIOAppArgs], Any, zioPlayground.type] = ???
//
//  override def run: ZIO[zioPlayground.type with ZEnv with Has[ZIOAppArgs], Any, Any] = ???
//}
