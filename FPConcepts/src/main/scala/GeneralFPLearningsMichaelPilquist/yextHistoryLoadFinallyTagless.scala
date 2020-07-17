package GeneralFPLearningsMichaelPilquist

import java.io._
import java.text.SimpleDateFormat
import java.util.Date

import net.liftweb.json.{ DefaultFormats, _ }
import scalaj.http.{ Http, HttpResponse }

import scala.io.StdIn.readLine
import scala.util.Try

object YextTimetrade extends App {

  //  trait Functor[F[_]] {
  //    def map[A, B](fa: F[A])(f: A => B): F[B]
  //
  //    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  //  }
  //
  //  //case class (url: )
  //  case class CancelledData(confirmationNumber: String, appointmentStart: String, appointmentEnd: String)
  //
  //  case class CancelledAppointments[CancelledData](elem: CancelledData) {
  //    def map[B](f: CancelledData => B): CancelledAppointments[B] =
  //      CancelledAppointments()
  //  }
  trait ChainCapability[F[_]] {
    def chain[A, B](fa: F[A])(f: A => F[B]): F[B]

    def map[A, B](fa: F[A])(f: A => B): F[B]

    def lift[A](a: => A): F[A]

    def eval[A](fa: F[A]): A

    def point[A]: F[Unit]
  }

  def point[F[_], A](implicit F: ChainCapability[F]) = F.point

  trait ExternalCapability[F[_]] {
    def printString(anyValue: Any): F[Unit]

    def readFromConsole(): F[String]

    def readFromAPI(url: String): F[HttpResponse[String]]

    def readFromAPIAsString(url: String): F[String]

    def writeToFile(lines: String, fileName: String): F[Unit]
  }

  object ExternalCapability {
    def apply[F[_]](implicit F: ExternalCapability[F]): ExternalCapability[F] =
      F
  }

  def printString[F[_]: ExternalCapability](anyValue: Any): F[Unit] =
    ExternalCapability[F].printString(anyValue)

  def readFromConsole[F[_]: ExternalCapability]: F[String] =
    ExternalCapability[F].readFromConsole()

  def readFromAPI[F[_]: ExternalCapability](
    url: String): F[HttpResponse[String]] = ExternalCapability[F].readFromAPI(url)

  def readFromAPIAsString[F[_]: ExternalCapability](url: String): F[String] =
    ExternalCapability[F].readFromAPIAsString(url)

  def writeToFile[F[_]: ExternalCapability](
    lines: String,
    fileName: String): F[Unit] =
    ExternalCapability[F].writeToFile(lines, fileName)

  trait StateChangeCapability[F[_]] {
    def incrementByOne[A](fa: F[A]): F[Int]
  }

  implicit class ExtensionForTypeZeroOfIntTypes[F[_]](fa: F[Int]) {
    def increment(implicit S: StateChangeCapability[F]): F[Int] =
      S.incrementByOne(fa)
  }
  implicit class ExtentionsForKindZeroTypes[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit F: ChainCapability[F]): F[B] =
      F.map(fa)(f)
    def flatMap[B](f: A => F[B])(implicit F: ChainCapability[F]): F[B] =
      F.chain(fa)(f)
    def eval(implicit F: ChainCapability[F]): A = F.eval(fa)
  }
  implicit def lift[F[_], A](a: => A)(implicit F: ChainCapability[F]): F[A] =
    F.lift(a)
  //  implicit def eval[F[_], A](implicit F: ChainCapability[F]): A = F.eval()

  abstract class State[S, A] {
    def map[B](fa: A => B): State[S, B] =
      State[S, B] { si: S =>
        val (s, a) = eval(si)
        (s, fa(a))
      }

    def flatMap[B](fa: A => State[S, B]): State[S, B] =
      State[S, B] { si: S =>
        val (s, a) = eval(si)
        fa(a).eval(s)
      }
    def eval(initialState: S): (S, A)
  }
  object State {
    def apply[S, A](f: S => (S, A)): State[S, A] =
      new State[S, A] {
        override def eval(initialState: S): (S, A) = f(initialState)
      }
  }

  implicit class extensionMethodsForState[S, A](fa: State[S, A]) {
    def otherMap[B](f: A => B): State[S, B] = fa.map(f)
    def otherFlatMap[B](f: A => State[S, B]): State[S, B] = fa.flatMap(f)
  }

  object StateCheck {
    def execute = {
      val evaledState = State[Int, Int](x => (x, 1)).eval(1)
      println(s"evaledState = $evaledState")
      val flatMappedState = State[Int, Int](x => (x, 1)).map(_ + 1).eval(1)
      println(s"flatMappedState = $flatMappedState")
      val otherFlatMappedState =
        State[Int, Int](x => (x, 1)).otherMap(_ + 1).eval(1)
      println(s"otherFlatMappedState = $otherFlatMappedState")

      val state1 = State[Int, Int](x => (x, 1))
      val stateBuild = for {
        s1 <- state1.map(_ + 1)
      } yield s1
      print(stateBuild.eval(2))
    }
  }
  StateCheck.execute

  case class SafeRunAPI[+Action](action: () => Action)
  //  {
  //    def map[B](f: Action => B): SafeRunAPI[B] = SafeRunAPI(() => f(action()))
  //    def flatMap[B](f: Action => SafeRunAPI[B]): SafeRunAPI[B] =
  //      SafeRunAPI(() => f(action()).action())
  //    def lift[A](a: => A): SafeRunAPI[Unit] = SafeRunAPI(() => ())
  //  }
  object SafeRunAPI {

    implicit val SafeRunApiWithChainingCapability: ChainCapability[SafeRunAPI] =
      new ChainCapability[SafeRunAPI] {
        def chain[A, B](
          fa: SafeRunAPI[A])(f: A => SafeRunAPI[B]): SafeRunAPI[B] =
          SafeRunAPI(() => f(fa.action()).action())

        def map[A, B](fa: SafeRunAPI[A])(f: A => B): SafeRunAPI[B] =
          SafeRunAPI(() => f(fa.action()))

        def lift[A](a: => A): SafeRunAPI[A] =
          SafeRunAPI(() => a)

        def eval[A](fa: SafeRunAPI[A]): A = fa.action()

        def point[A]: SafeRunAPI[Unit] = SafeRunAPI(() => ())
      }

    implicit val SafeRunApiWithExternalCapability: ExternalCapability[SafeRunAPI] =
      new ExternalCapability[SafeRunAPI] {
        override def printString(anyValue: Any): SafeRunAPI[Unit] =
          SafeRunAPI(() => println(anyValue))

        override def readFromConsole(): SafeRunAPI[String] =
          SafeRunAPI(() => readLine())

        override def readFromAPI(
          url: String): SafeRunAPI[HttpResponse[String]] =
          SafeRunAPI(() => Http(url).asString)

        override def readFromAPIAsString(url: String): SafeRunAPI[String] =
          SafeRunAPI(() => {
            val yextData = scala.io.Source.fromURL(url).mkString
            yextData
          })

        override def writeToFile(
          lines: String,
          fileName: String): SafeRunAPI[Unit] =
          SafeRunAPI { () =>
            {
              val pw: PrintWriter = new PrintWriter(new File(fileName))
              pw.write(lines)
              pw.close()
            }
          }
      }

    implicit val stateChangeCapabilityWithSafeRunAPI: StateChangeCapability[SafeRunAPI] =
      new StateChangeCapability[SafeRunAPI] {
        override def incrementByOne[A](
          fa: SafeRunAPI[A]): SafeRunAPI[Int] =
          SafeRunAPI(() => fa.action().asInstanceOf[Int] + 1)
      }
  }

  def parseHttpResponse[F[_]: ChainCapability: ExternalCapability](
    value: F[HttpResponse[String]]): F[Either[String, Map[String, Any]]] = {
    for {
      response <- value
      //      _ <- printString(response)
      countEither <- if (response.isSuccess)
        Right {
          (parse(response.body) \\ "response" \\ "count").values
        }
      else Left("Error while fetching the details from Yext")
    } yield countEither
  }

  def matchEitherOfCount[F[_]: ExternalCapability](
    countEither: Either[String, Map[String, Any]])(implicit FA: ChainCapability[F]): F[BigInt] = countEither match {
    case Right(value) =>
      FA.lift {
        value
          .getOrElse("count", 0)
          .asInstanceOf[BigInt]
      }
    case Left(error) =>
      printString(error)
      FA.lift(0)
  }

  def getYextHistoryDataTotalRecordCount[F[_]: ChainCapability: ExternalCapability](
    url: String): F[BigInt] =
    for {
      urlString <- readFromAPI(url)
      //      _ <- printString(urlString)
      countEither <- parseHttpResponse(lift(urlString))
      count <- matchEitherOfCount(countEither)
      _ <- printString(count)
    } yield count

  type YextRecords[F[_]] = F[HttpResponse[String]]
  def todayDate(dateFormat: String): Option[String] = {
    val todayDate: Option[String] = {
      val date = new Date
      val sdf = new SimpleDateFormat(dateFormat)
      Try(sdf.format(date)).toOption
    }
    todayDate
  }
  def doLoop[F[_]: ExternalCapability: ChainCapability](
    filePath: String,
    totalCount: BigInt): F[Unit] = {
    //    val todayDate = java.time.LocalDate.now
    val dateFmt = "yyyyMMdd"
    val todayDate: String = {
      val date = new Date
      val sdf = new SimpleDateFormat(dateFmt)
      sdf.format(date)
    }
    val limit = 50
    var offset = 1
    var index = 0
    while (offset <= totalCount) {
      val incUrl =
        s"""https://liveapi.yext.com/v2/accounts/me/locations?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=${todayDate}&&limit=${limit}&offset=${offset}"""
      val fileName = s"""${filePath}yext-history-${todayDate}-part-${index}"""
      val res: F[Unit] = for {
        _ <- printString(s"Executing the url string ${incUrl}")
        data <- readFromAPIAsString(incUrl)
        //        _ <- printString(data)
        _ <- writeToFile(data, fileName)
        _ <- printString(s"Successfully written to the file ${fileName}")
      } yield ()
      res.eval
      offset += limit
      index += 1
    }
  }

  //  def doLoop1[F[_]: ExternalCapability: ChainCapability](
  //    filePath: String,
  //    totalCount: BigInt): F[Unit] = {
  //    //    val todayDate = java.time.LocalDate.now
  //    val dateFmt = lift[F, String]("yyyyMMdd")
  //    val dateFormat = "yyyyMMdd"
  //    for {
  //      todayDate <- todayDate(dateFormat) match {
  //        case Some(date) => lift[F, String](date)
  //        case None => point[F, Unit]
  //        case _ => point[F, Unit]
  //      }
  //      offset = 1
  //      _ <- lift(offset)
  //    } yield ()
  //    val limit = 50
  //    var offset = 1
  //    var index = 0
  //    while (offset <= totalCount) {
  //      val incUrl =
  //        s"""https://liveapi.yext.com/v2/accounts/me/locations?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=${todayDate}&&limit=${limit}&offset=${offset}"""
  //      val fileName = s"""${filePath}yext-history-${todayDate}-part-${index}"""
  //      val res: F[Unit] = for {
  //        _ <- printString(s"Executing the url string ${incUrl}")
  //        data <- readFromAPIAsString(incUrl)
  //        //        _ <- printString(data)
  //        _ <- writeToFile(data, fileName)
  //        _ <- printString(s"Successfully written to the file ${fileName}")
  //      } yield ()
  //      res.eval
  //      offset += limit
  //      index += 1
  //    }
  //  }

  def writeToFile[F[_]: ChainCapability: ExternalCapability](
    //      records: YextRecords[F],
    filePathF: F[String],
    url: String): F[Unit] = {
    for {
      totalCount <- getYextHistoryDataTotalRecordCount(url)
      filePath <- filePathF
      writeResponse <- doLoop(filePath, totalCount)
    } yield ()
  }

  def filePath: SafeRunAPI[String] =
    SafeRunAPI(() => "C:\\Users\\kkalya622\\Documents\\temporary_workspace\\")
  val yextEndpointUrl =
    "https://liveapi.yext.com/v2/accounts/me/locations?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=20200505&&limit=1&offset=1"
  val writeToFileSafeRun: SafeRunAPI[Unit] =
    writeToFile[SafeRunAPI](filePath, yextEndpointUrl)

  //  writeToFileSafeRun.action()
}
