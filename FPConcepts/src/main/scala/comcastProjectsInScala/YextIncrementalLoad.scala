package comcastProjectsInScala

import ujson._
import upickle._
import io.circe._, io.circe.parser._

import scala.io.BufferedSource

case class ParsedInput(
  store_number_rt: String,
  store_name_rt: String,
  address_txt_rt: String,
  location_name_rt: String,
  country_code_rt: String,
  city_name_rt: String,
  state_code_rt: String,
  zip_code_rt: String,
  store_region_name_rt: String,
  pages_url_rt: String,
  custom_appointment_link_rt: String,
  product_desc_rt: String,
  branded_partner_flag_rt: String,
  store_open_date_rt: String,
  store_timezone_desc_rt: String,
  longitude_rt: String,
  latitude_rt: String,
  phone_number_rt: String,
  local_phone_rt: String,
  id_rt: String,
  timestamp_utc_rt: String,
  store_hours_rt: String,
  holiday_hours_rt: String,
  additional_hours_text_rt: String,
  is_closed_rt: String,
  reopen_date_rt: String)

object YextIncrementalLoad extends App {
  val event: BufferedSource = scala.io.Source.fromFile("FPConcepts/src/main/resources/Yext-incremental-input-event.json")
  val event_string: String = event.mkString
  println(event_string)
  event.close

  trait Externals[A]{
    def failures : Unit
  }

  class Database
  class Redshift(val dbName: String,val hostName: String ) extends Externals[Database]{
    override def failures: Unit = println("Failed to connect to redshift")
  }

  case class Parser[A](readFromSource: A)

  abstract class Monad[F[_]]{
    def flatMap[A,B](fa: F[A])(f: A => F[B]) :F[B]
    def map[A,B](fa: F[A])(f: A => B) :F[B]
    def pure[A](a: A) :F[A]
  }

  object Parser {
    val flatMapForParser: Monad[Parser] = new Monad{
      override def flatMap[A, B](fa: Parser[A])(f: A => Parser[B]): Parser[B] = f(fa.readFromSource)

      override def map[A, B](fa: Parser[A])(f: A => B): Parser[B] = pure(f(fa.readFromSource))

      override def pure[A](a: A): Parser[A] = Parser(a)
    }
  }

  import Parser.flatMapForParser._
//  for {
//    _ <- flatMap()
//  }
}
