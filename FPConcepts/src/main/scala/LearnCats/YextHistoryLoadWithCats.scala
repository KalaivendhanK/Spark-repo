package LearnCats

import simulacrum.{ typeclass, _ }
import cats.{ Monad, Semigroup }
import cats.implicits._
import spray.json._
import java.io.{ File, PrintWriter }
import java.util.Calendar
import java.text.SimpleDateFormat

import LearnCats.YextHistoryLoadWithCats.loop

object YextHistoryLoadWithCats extends App {
  @typeclass trait Monad[F[_]] {
    def lift[A](a: A): F[A]
    /* @op("<->") */ def map[A, B](fa: F[A])(f: A => B): F[B]
    /* @op("<<->>") */ def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  }
  //      implicit class ExtensionForIOMOnads[A](ioa: IO[A]){
  //        def lift(a: A)(implicit IOM: Monad[IO]): IO[A] = IOM.lift(a)
  //        def map[B](f: A => B)(implicit IOM: Monad[IO]):IO[B] = IOM.map(ioa)(f)
  //        def flatMap[B](f: A => IO[B])(implicit  IOM: Monad[IO]): IO[B] = IOM.flatMap(ioa)(f)
  //      }
  def lift[F[_], A](a: => A)(implicit M: Monad[F]): F[A] = M.lift(a)

  trait ApiRequests[F[_]] {
    def getAsString(url: String): F[String]
    def getRequests(url: String): F[JsValue]
    def getCountField(parsedData: JsValue): F[Int]
  }
  def getAsString[F[_]](url: String)(implicit F: ApiRequests[F]): F[String] = F.getAsString(url)
  def getRequests[F[_]](url: String)(implicit F: ApiRequests[F]): F[JsValue] = F.getRequests(url)
  def getCountField[F[_]](parsedData: JsValue)(implicit F: ApiRequests[F]): F[Int] = F.getCountField(parsedData)

  trait ExternalInteractions[F[_]] {
    def writeData(location: String)(url: String): F[Unit]
    def printToConsole(str: String): F[Unit]
    def readFromConsole: F[String]
  }
  def writeData[F[_]](location: String)(url: String)(implicit F: ExternalInteractions[F]): F[Unit] = F.writeData(location)(url)
  def printToConsole[F[_]](str: String)(implicit F: ExternalInteractions[F]): F[Unit] = F.printToConsole(str)
  def readFromConsole[F[_]](implicit F: ExternalInteractions[F]): F[String] = F.readFromConsole

  import Monad.ops._

  //  implicit class combinatorForKindOneTypes[F[_],A ](fa: F[A]){
  //    def map[B](f: A => B)(implicit C: Monad[F]) = C.map(fa)(f)
  //    def flatMap[B](f:A => F[B])(implicit C: Monad[F]): F[B] = C.flatMap(fa)(f)
  //  }
  //  IO Case class to capture the effects
  case class IO[A](run: () => A)

  //   IO Instances for Monad and ApiRequests Type classes
  object IO {
    implicit val monadForIO: Monad[IO] = new Monad[IO] {
      override def lift[A](a: A): IO[A] = IO(() => a)
      override def map[A, B](fa: IO[A])(f: A => B): IO[B] =
        IO(() => f(fa.run()))
      override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
        IO(() => f(fa.run()).run())
    }
    implicit def instanceForApiRequests: ApiRequests[IO] = new ApiRequests[IO] {
      override def getAsString(url: String): IO[String] = IO(() => scala.io.Source.fromURL(url).mkString)
      override def getRequests(url: String): IO[JsValue] = IO(() => (scala.io.Source.fromURL(url).mkString).parseJson)
      override def getCountField(parsedData: JsValue): IO[Int] = IO(() => parsedData.asJsObject.getFields("response").head.asJsObject.getFields("count").head.toString().toInt)
    }
    implicit def instanceForExternalInteractions: ExternalInteractions[IO] = new ExternalInteractions[IO] {
      override def writeData(location: String)(url: String): IO[Unit] = for {
        filePath <- lift(s"C:\\Users\\kkalya622\\Documents\\temporary_workspace")
        writer = new PrintWriter(new File(s"${filePath}\\${location}"))
        data <- getAsString(url)
        _ = writer.write(data)
        _ <- printToConsole(s"Done writing into the file ${location}")
        _ = writer.close()
      } yield ()
      override def printToConsole(str: String): IO[Unit] = IO(() => println(str))
      override def readFromConsole: IO[String] = IO(() => "") // TODO Implement the readline
    }
  }

  val getTodayDate = {
    val now = Calendar.getInstance().getTime()
    val dateFormatter = new SimpleDateFormat("yyyyMMdd")
    dateFormatter.format(now)
  }

  def loop[F[_]: Monad: ExternalInteractions](url: String)(fileCount: Int): F[Unit] = {
    for {
      fileName <- lift(s"yext-history-${getTodayDate}-part-${fileCount}")
      _ <- lift(println(s"${fileName}"))
      _ <- writeData(fileName)(url)
      //      _ <- fileName.writeData(url)
    } yield ()
  }

  def getDataRecursively[F[_]: Monad: ExternalInteractions](url: String)(count: Int, offset: Int, fileCount: Int): F[Unit] = {
    for {
      limit <- lift(50)
      _ <- if (offset > count) lift(())
      else
        for {
          url <- lift(s"https://liveapi.yext.com/v2/accounts/me/entities?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=${getTodayDate}&&limit=${limit}&offset=${offset}&entityTypes=location")
          _ <- loop[F](url)(fileCount)
          _ <- getDataRecursively(url)(count, offset + limit, fileCount + 1)
        } yield ()
    } yield ()
  }

  def requestAndParse[F[_]: Monad: ApiRequests: ExternalInteractions]: F[Unit] = for {
    offset <- lift(1)
    limit <- lift(1)
    urlToFetchTotalCounts = s"https://liveapi.yext.com/v2/accounts/me/entities?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=${getTodayDate}&&limit=${limit}&offset=${offset}&entityTypes=location"
    parsedData <- getRequests(urlToFetchTotalCounts)
    count <- getCountField(parsedData)
    _ <- getDataRecursively("")(count, offset, 0)
  } yield ()

  val safeMain = requestAndParse[IO]
  safeMain.run()

  // Imperitive implementation of the yext history process
  //  unSafemain
  def unSafemain: Unit = {
    val getTodayDate = {
      val now = Calendar.getInstance().getTime()
      val dateFormatter = new SimpleDateFormat("yyyyMMdd")
      dateFormatter.format(now)
    }
    val yextEndpoint = "https://liveapi.yext.com/v2/accounts/me/entities?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=20200601&&limit=1&offset=1&entityTypes=location"
    def get(url: String): String = scala.io.Source.fromURL(url).mkString
    val parsedData: JsValue = get(yextEndpoint).parseJson
    val count = parsedData.asJsObject.getFields("response").head.asJsObject.getFields("count").head.toString().toInt
    println(s"count = ${count}")
    val limit = 50
    var offset = 1
    var i = 0
    while (offset < count) {
      val url = s"https://liveapi.yext.com/v2/accounts/me/entities?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=${getTodayDate}&&limit=${limit}&offset=${offset}&entityTypes=location"
      val fileName = s"yext-history-${getTodayDate}-part-${i}"
      val filePath = s"C:\\Users\\kkalya622\\Documents\\temporary_workspace"
      val writer = new PrintWriter(new File(s"${filePath}\\${fileName}"))
      writer.write(get(url))
      println(s"Done writing into the file ${fileName}")
      writer.close()
      offset += limit
      i += 1
    }
  }

  // Practice: Typeclass instances for ZeroKindedTypes
  @typeclass trait MonadForSingleTypes[A] {
    def map[B](fa: A)(f: A => B): B
  }
  object MonadForSingleTypes {
    implicit val monadForInt: MonadForSingleTypes[Int] = new MonadForSingleTypes[Int] {
      override def map[B](fa: Int)(f: Int => B): B = f(fa)
    }
    implicit val monadForChar: MonadForSingleTypes[Char] = new MonadForSingleTypes[Char] {
      override def map[B](fa: Char)(f: Char => B): B = f(fa)
    }
  }
  //  Semigroup[Int].combine(1, 2)
}
