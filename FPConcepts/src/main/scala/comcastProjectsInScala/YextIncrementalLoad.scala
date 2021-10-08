package comcastProjectsInScala

import ujson._
import upickle._
import io.circe._, io.circe.parser._

import scala.io.BufferedSource

case class ParsedInput(store_number_rt: String, store_name_rt: String, address_txt_rt: String, location_name_rt: String, country_code_rt: String, city_name_rt: String, state_code_rt: String, zip_code_rt: String, store_region_name_rt: String, pages_url_rt: String, custom_appointment_link_rt: String, product_desc_rt: String, branded_partner_flag_rt: String, store_open_date_rt: String, store_timezone_desc_rt: String, longitude_rt: String, latitude_rt: String, phone_number_rt: String, local_phone_rt: String, id_rt: String, timestamp_utc_rt: String, store_hours_rt: String, holiday_hours_rt: String, additional_hours_text_rt: String, is_closed_rt: String, reopen_date_rt: String)

object YextIncrementalLoad extends App {

  trait TextString {
    def text: String
  }

  object HardCode {

    case class YextFileName() extends TextString {
      override def text: String = "FPConcepts/src/main/resources/Yext-incremental-input-event.json"
    }
  }
  trait Externals[A] {
    def failures(): Unit
  }

  class Database
  class Redshift(val dbName: String, val hostName: String) extends Externals[Database] {
    override def failures(): Unit = println("Failed to connect to redshift")
  }

  case class Parser[+A](readFromSource: A)

  case class IO[+A](run: () ⇒ A)

  abstract class Monad[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A ⇒ F[B]): F[B]
    def map[A, B](fa: F[A])(f: A ⇒ B): F[B]
    def pure[A](a: A): F[A]
  }

  object Monad {
    def apply[F[_]](implicit M: Monad[F]): Monad[F] = M

    implicit val IOInstanceForMonad: Monad[IO] = new Monad[IO] {
      override def flatMap[A, B](fa: IO[A])(f: A ⇒ IO[B]): IO[B] = IO {
        () ⇒ f(fa.run()).run()
      }
      override def map[A, B](fa: IO[A])(f: A ⇒ B): IO[B] = IO {
        () ⇒ f(fa.run())
      }
      override def pure[A](a: A): IO[A] = IO {
        () ⇒ a
      }
    }
  }
  abstract class FileOperations[F[_]] {
    def readFromFile(fileName: String): F[BufferedSource]
    def closeFileBuffers(bufferedSource: BufferedSource): F[Unit]
  }
  def readFromFile[F[_]](fileName: String)(implicit FO: FileOperations[F]): F[BufferedSource] =
    FO.readFromFile(fileName)

  def closeFileBuffers[F[_]](bufferedSource: BufferedSource)(implicit FO: FileOperations[F]): F[Unit] =
    FO.closeFileBuffers(bufferedSource)

  object FileOperations {
    def apply[F[_]](implicit FO: FileOperations[F]): FileOperations[F] = FO

    implicit val IOInstanceForFileOperations: FileOperations[IO] = new FileOperations[IO] {
      override def readFromFile(fileName: String): IO[BufferedSource] = IO { () ⇒
        scala.io.Source.fromFile(fileName)
      }
      override def closeFileBuffers(bufferedSource: BufferedSource): IO[Unit] = IO { () ⇒
        bufferedSource.close()
      }
    }
  }

  trait Console[F[_]] {
    def readString: F[String]
    def printString(str: String): F[Unit]
  }
  object Console {
    def apply[F[_]](implicit C: Console[F]): Console[F] = C

    implicit val IOInstanceForConsole: Console[IO] = new Console[IO] {
      def readString: IO[String] = IO(() ⇒ "") //TODO: Implement read from console afterwards
      def printString(str: String): IO[Unit] = IO(() ⇒ println(str))
    }
  }

  trait StringOperations[F[_]] {
    def parseString(str: String): F[String]
  }

  object StringOperations {
    def apply[F[_]](implicit SO: StringOperations[F]): StringOperations[F] = SO

    implicit val IOInstanceForStringOperations: StringOperations[IO] = new StringOperations[IO] {
      private val daysInAWeek: List[String] = List("monday", "tuesday", "wednesday")
      private def frameStoreHours(days: ACursor): String = {
        val mondayHours = days.downField("monday").downField("openIntervals").downArray
        val mondayStart = mondayHours.downField("start").focus.getOrElse("not found monday start time")
        val mondayEnd = mondayHours.downField("end").focus.getOrElse("not found monday end time")
        println((mondayStart + ":" + mondayEnd).replace("\"", ""))
        //          val mondayStart = mondayHours.
        ""
      }
      def parseString(str: String): IO[String] = parse(str) match {
        case Left(errorString: ParsingFailure) ⇒ IO(() ⇒ s"Error while parsing the input: $errorString")
        case Right(parsedString) ⇒ IO(() ⇒ {
          //            case class ParsedInput(store_number_rt: String, store_name_rt: String, address_txt_rt: String, location_name_rt: String, country_code_rt: String, city_name_rt: String, state_code_rt: String, zip_code_rt: String, store_region_name_rt: String, pages_url_rt: String, custom_appointment_link_rt: String, product_desc_rt: String, branded_partner_flag_rt: String, store_open_date_rt: String, store_timezone_desc_rt: String, longitude_rt: String, latitude_rt: String, phone_number_rt: String, local_phone_rt: String, id_rt: String, timestamp_utc_rt: String, store_hours_rt: String, holiday_hours_rt: String, additional_hours_text_rt: String, is_closed_rt: String, reopen_date_rt: String)
          val cursor: HCursor = parsedString.hcursor
          val storeNumberJson: Decoder.Result[String] = cursor.downField("primaryProfile").downField("c_storeNumber").as[String]
          val storeNameJson: Decoder.Result[String] = cursor.downField("primaryProfile").downField("c_localName").as[String]
          val storeAddressJson: Decoder.Result[String] = cursor.downField("primaryProfile").downField("address").downField("line1").as[String]
          val locationNameJson: Decoder.Result[String] = cursor.downField("primaryProfile").downField("name").as[String]
          val storeHoursCursor: ACursor = cursor.downField("primaryProfile").downField("hours")
          println(cursor.downField("primaryProfile").downField("hours").downField("monday").downField("openIntervals").downArray.focus)

          val storeNumber: String = storeNumberJson.toOption.getOrElse("not found storeNumber")
          val storeName: String = storeNameJson.toOption.getOrElse("not found storeName")
          val storeAddress: String = storeAddressJson.toOption.getOrElse("not found storeAddress")
          val locationName: String = locationNameJson.toOption.getOrElse("not found locationName")
          val storeHours: String = frameStoreHours(storeHoursCursor)
          (storeNumber, storeName, storeAddress, locationName, storeHours).toString()
        })
      }
    }
  }

  implicit class ExtensionForTypeOne[F[_], A](fa: F[A]) {
    def flatMap[B](f: A ⇒ F[B])(implicit M: Monad[F]): F[B] = M.flatMap(fa)(f)
    def map[B](f: A ⇒ B)(implicit M: Monad[F]): F[B] = M.map(fa)(f)
    def pure(a: A)(implicit M: Monad[F]): F[A] = M.pure(a)
  }

  lazy val fileName: String = HardCode.YextFileName().text

  def makeBufferedSourceAsString(bufferedSource: BufferedSource): String = bufferedSource.mkString
  def main[F[_]: Monad: FileOperations: Console: StringOperations]: F[Unit] = for {
    bufferedSource ← FileOperations[F].readFromFile(fileName)
    //     _              <- Console[F].printString(makeBufferedSourceAsString(bufferedSource))
    parsedString ← StringOperations[F].parseString(makeBufferedSourceAsString(bufferedSource))
    _ ← Console[F].printString(parsedString)
    _ ← FileOperations[F].closeFileBuffers(bufferedSource)
  } yield ()

  def runApp: IO[Unit] = main[IO]

  runApp.run()

  object Parser {
    val flatMapForParser: Monad[Parser] = new Monad[Parser] {
      override def flatMap[A, B](fa: Parser[A])(f: A ⇒ Parser[B]): Parser[B] = f(fa.readFromSource)

      override def map[A, B](fa: Parser[A])(f: A ⇒ B): Parser[B] = pure(f(fa.readFromSource))

      override def pure[A](a: A): Parser[A] = Parser(a)
    }
  }

  //    import Parser._
  //    for {
  //      _ <- flatMap()
  //    }
}
