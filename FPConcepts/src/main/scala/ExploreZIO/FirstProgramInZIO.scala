package ExploreZIO

import zio.{ console, App, ZEnv, ZIO }
import zio.console.Console
object FirstProgramInZIO extends App {
  def program: ZIO[Console, String, Unit] = console.putStrLn("Hello ZIO")
  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    program.foldM(
      error => console.putStrLn(s"There is an error while printing in console. $error") *> ZIO.succeed(1), _ => ZIO.succeed(1))
  }
}
