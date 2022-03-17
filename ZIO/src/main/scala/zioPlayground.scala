
import zio.ZIOAspect.debug.@@
import zio._
import zio.ZIOAspect._

object anObject extends ZIOAppDefault {

  /**
    * ZIO 2.0 Service layer implementation
    */
  trait Print {
    def print: ZIO[Any, Nothing, Unit]
  }

  object Print {
    val live: ZLayer[Any, Nothing, Print] =
      ZLayer.succeed(new Print {
        override def print: ZIO[Any, Nothing, Unit] =
          ZIO.succeed(println("Hello this is a print statement to check the zio functionality"))
      })
  }

  val businessLogic: ZIO[Print, Nothing, Unit] =
    for {
    printService <- ZIO.service[Print]
    printFiber <- printService.print.fork
    _ <- printFiber.join
  } yield ()

  val implWithLayer: ZIO[Any, Nothing, Unit] = businessLogic.provide(Print.live)

  override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any] = implWithLayer.exitCode

}