package comcastProjectsInScala

import ujson._
import upickle._

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
class YextIncrementalLoad {
  //  def loadInputEventFromFile(fileName: String): ParsedInput ={
  //    @ val jsonString = os.read(os.pwd / "ammonite-releases.json")
  //    ParsedInput(???)
  //    ???
  //  }
  val event: BufferedSource = scala.io.Source.fromFile("FPConcepts/src/main/resources/Yext-incremental-input-event.json")
  val event_string: String = event.mkString
  println(event_string)
  event.close
}
