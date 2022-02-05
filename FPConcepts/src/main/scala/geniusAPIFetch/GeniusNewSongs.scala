package geniusAPIFetch

import scalaj.http.{Http, HttpOptions, HttpResponse}
import zio._
import zio.console.{Console, putStrLn}

object GeniusNewSongs extends zio.App{

  val start_id = 7423700
  val url = s"https://api.genius.com/songs/${start_id}"

  val token_str = "ad_QwuFLmvAqx5-dDzwx2RsDphAoDmFCS7XNWBXiwVt4Xnumz2c-FwRMQFYaaaZC"

//  headers = { 'Authorization ': 'Bearer {} '.format(token_str) , 'Content - Type ': 'application / json ' }

  def request: HttpResponse[String] = Http(s"$url")
    .header("Authorization", s"Bearer $token_str")
    .header("Content-Type", "application/json")
    .asString

  def runJob: ZIO[Console, Throwable, Unit] = for {
//    r <- ZIO.foreachParN(1)(1 to 100)(x => ZIO.succeed(request))
//    _ <- ZIO.foreach(r)(httpResp =>
//          putStrLn(s"${httpResp.isSuccess}")
//          )
    r: HttpResponse[String] <- ZIO.succeed(request)
    _ <- putStrLn(s"${r.isSuccess}")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = runJob.exitCode
}
