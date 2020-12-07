package GeneralFPLearningsJohnADoes.ExploreZIO

//import zio.{ console, App, ZEnv, ZIO }
//import zio.console.Console
import zio._
import console._
object FirstProgramInZIO extends App {
  def program: ZIO[Console, String, Unit] = putStrLn("Hello ZIO")

  def run(args: List[String]): URIO[ZEnv, ExitCode] = {
//    program *> ZIO.succeed(1)
//    ExitCode(1)
    ???
  }
}
