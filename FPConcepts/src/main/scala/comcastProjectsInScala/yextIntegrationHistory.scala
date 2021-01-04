package comcastProjectsInScala

import scalaj.http.{ Http, HttpResponse }

/**
  * Fetch the data from Yext
  * Below is implemented by my own logic
  */
object YextIntegrationHistory1 extends App {
  //  def buildFunctions[A](obj: A): () => A =
  //    () => obj
  //  val urlFunction: () => String = buildFunctions(yextEndPointUrl)
  trait Requests {
    def build(url: ⇒ String): ExternalRequests[HttpResponse[String]]
    def trigger(
        http: ⇒ ExternalRequests[HttpResponse[String]]
    ): HttpResponse[String]
  }

  case class ExternalRequests[R](elem: () ⇒ R) extends Requests {
    override def build(url: ⇒ String): ExternalRequests[HttpResponse[String]] =
      ExternalRequests[HttpResponse[String]](() ⇒ Http(url).asString)

    override def trigger(
        httpRequest: ⇒ ExternalRequests[HttpResponse[String]]
    ): HttpResponse[String] =
      httpRequest.execute

    def execute: R = this.elem()

    def show(): Unit = println(execute)
  }

  object ExternalRequests {
    final def apply(): ExternalRequests[Unit] = ExternalRequests(() ⇒ ())
  }

  val yextEndPointUrl =
    "https://liveapi.yext.com/v2/accounts/me/locations?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=20200505&&limit=1&offset=1"
  val getResponse: HttpResponse[String] =
    ExternalRequests().build(yextEndPointUrl).execute
  println(getResponse)
  //  ExternalRequests().build(yextEndPointUrl).show
}

/**
  * Fetch the data from Yext
  * Functions are outside of the case classes in the below implentation
  * Each function returns a function that gets invoked later
  */
object YextIntegrationHistory2 extends App {
  case class ExternalRequests[R](elem: () ⇒ R)

  def build(url: ⇒ String): ExternalRequests[HttpResponse[String]] =
    ExternalRequests[HttpResponse[String]](() ⇒ Http(url).asString)

  val yextEndPointUrl =
    "https://liveapi.yext.com/v2/accounts/me/locations?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=20200505&&limit=1&offset=1"
  val getResponse: HttpResponse[String] =
    build(yextEndPointUrl).elem()

  println(getResponse)
}

/**
  * Fetch the data from Yext
  * Functions are outside of the case classes in the below implementation
  * Each function returns a function that gets invoked later
  * Using for comprehensions by implementing map and flatmap functions
  * Courtesy from John A De Goes youtube video
  */
object YextIntegrationHistory3 extends App {
  case class ExternalRequests[R](elem: () ⇒ R) {
    final def map[B](f: R ⇒ B): ExternalRequests[B] =
      ExternalRequests(() ⇒ f(elem()))
    final def flatMap[B](f: R ⇒ ExternalRequests[B]): ExternalRequests[B] =
      ExternalRequests(() ⇒ f(elem()).elem())
  }

  object ExternalRequests {
    final def apply(): ExternalRequests[Unit] = ExternalRequests(() ⇒ ())
  }

  def build(url: ⇒ String): ExternalRequests[HttpResponse[String]] =
    ExternalRequests[HttpResponse[String]](() ⇒ Http(url).asString)

  def printToConsole(s: Any): ExternalRequests[Unit] =
    ExternalRequests(() ⇒ println(s))
  val yextEndPointUrl =
    "https://liveapi.yext.com/v2/accounts/me/locations?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=20200505&&limit=1&offset=1"

  def main: ExternalRequests[Unit] =
    for {
      response ← build(yextEndPointUrl)
      _ ← printToConsole(response)
    } yield ()

  main.elem()
}

object YextIntegrationHistory4 extends App {
  trait Requests {
    def build(url: ⇒ String): ExternalRequests[HttpResponse[String]]
    def trigger(
        http: ⇒ ExternalRequests[HttpResponse[String]]
    ): HttpResponse[String]
  }

  case class ExternalRequests[R](elem: () ⇒ R) {
    final def map[B](f: R ⇒ B): ExternalRequests[B] =
      ExternalRequests(() ⇒ f(elem()))
    final def flatMap[B](f: R ⇒ ExternalRequests[B]): ExternalRequests[B] =
      ExternalRequests(() ⇒ f(elem()).elem())
  }

  object ExternalRequests {
    final def apply(): ExternalRequests[Unit] = ExternalRequests(() ⇒ ())
  }

  def build(url: ⇒ String): ExternalRequests[HttpResponse[String]] =
    ExternalRequests[HttpResponse[String]](() ⇒ Http(url).asString)

  def printToConsole(s: Any): ExternalRequests[Unit] =
    ExternalRequests(() ⇒ println(s))
  val yextEndPointUrl =
    "https://liveapi.yext.com/v2/accounts/me/locations?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=20200505&&limit=1&offset=1"

  def main: ExternalRequests[Unit] =
    for {
      response ← build(yextEndPointUrl)
      _ ← printToConsole(response)
    } yield ()

  main.elem()
}
