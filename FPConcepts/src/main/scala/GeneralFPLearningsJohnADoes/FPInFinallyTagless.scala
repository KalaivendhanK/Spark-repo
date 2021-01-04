package GeneralFPLearningsJohnADoes

import scala.io.StdIn.readLine
import scala.util.Try

object App0 extends App {
  def main(): Unit = {
    println("What is your name?")

    val name = readLine()

    println("Hello, " + name + ", welcome to the game!")

    var exec = true

    while (exec) {
      val num = scala.util.Random.nextInt(5) + 1

      println("Dear " + name + ", please guess a number from 1 to 5:")

      val guess = readLine().toInt

      if (guess == num) println("You guessed right, " + name + "!")
      else println("You guessed wrong, " + name + "! The number was: " + num)

      println("Do you want to continue, " + name + "?")

      readLine() match {
        case "y" ⇒ exec = true
        case "n" ⇒ exec = false
      }
    }
  }

  main()
}

object App1 extends App {
  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  case class IO[A](val runUnreliableCode: () ⇒ A) {
    def map[B](f: A ⇒ B): IO[B] = IO(() ⇒ f(this.runUnreliableCode()))
    def flatMap[B](f: A ⇒ IO[B]): IO[B] =
      IO(() ⇒ f(this.runUnreliableCode()).runUnreliableCode())
  }
  object IO {
    def point[A](a: ⇒ A): IO[A] = IO(() ⇒ a)
  }

  def printString(s: String): IO[Unit] = IO(() ⇒ println(s))

  def getString: IO[String] = IO(() ⇒ readLine())

  def generateRandomNumber(num: Int): IO[Int] =
    IO(() ⇒ scala.util.Random.nextInt(num))

  def checkRepeat(name: String): IO[Boolean] =
    for {
      _ ← printString("Do you want to continue, " + name + "?")
      input ← getString map (_.toLowerCase())
      cont ← input match {
        case "y" ⇒ IO.point(true)
        case "n" ⇒ IO.point(false)
        case _   ⇒ checkRepeat(name)
      }
    } yield cont

  def gameLoop(name: String): IO[Unit] =
    for {
      num ← generateRandomNumber(5) map (_ + 1)
      _ ← printString("Dear " + name + ", please guess a number from 1 to 5:")
      input ← getString
      _ ← parseInt(input) match {
        case None ⇒
          printString(
            "You did not enter a valid number .Please input a valid number"
          )
        case Some(value) ⇒
          if (value == num) printString("You guessed right, " + name + "!")
          else
            printString(
              "You guessed wrong, " + name + "! The number was: " + num
            )
      }
      cont ← checkRepeat(name)
      _ ← if (cont) gameLoop(name) else IO.point(())
    } yield ()

  def main1: IO[Unit] =
    for {
      _ ← printString("What is your name?")
      name ← getString
      _ ← printString("Hello, " + name + ", welcome to the game!")
      _ ← gameLoop(name)
    } yield ()

  def main: IO[Unit] =
    printString("What is your name?") flatMap { _ ⇒
      getString flatMap { name ⇒
        printString("Hello, " + name + ", welcome to the game!") flatMap { _ ⇒
          gameLoop(name)
        }
      }
    }

  main.runUnreliableCode()
}

object App2 extends App {
  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  trait Program[F[_]] {
    def finish[A](a: ⇒ A): F[A]
    def chain[A, B](fa: F[A], afb: A ⇒ F[B]): F[B]
    def map[A, B](fa: F[A], ab: A ⇒ B): F[B]
  }

  object Program {
    def apply[F[_]](implicit f: Program[F]): Program[F] = f
  }
  implicit class ProgramExtension[F[_], A](f: F[A]) {
    def flatMap[B](fa: A ⇒ F[B])(implicit F: Program[F]): F[B] =
      F.chain(f, fa)
    def map[B](fa: A ⇒ B)(implicit F: Program[F]): F[B] = F.map(f, fa)
    //    def point[B](fa: => B)(implicit F: Program[F]): F[B] = F.finish(fa)
  }
  def point[F[_], A](fa: ⇒ A)(implicit F: Program[F]): F[A] = F.finish(fa)

  trait Console[F[_]] {
    def printString(s: String): F[Unit]
    def getString: F[String]
  }

  object Console {
    def apply[F[_]](implicit f: Console[F]): Console[F] = f
  }

  trait Random[F[_]] {
    def generateRandomNumber(num: Int): F[Int]
  }
  object Random {
    def apply[F[_]](implicit f: Random[F]): Random[F] = f
  }

  case class IO[A](val runUnreliableCode: () ⇒ A) {
    def map[B](f: A ⇒ B): IO[B] = IO(() ⇒ f(this.runUnreliableCode()))
    def flatMap[B](f: A ⇒ IO[B]): IO[B] =
      IO(() ⇒ f(this.runUnreliableCode()).runUnreliableCode())
  }
  object IO {
    def point[A](a: ⇒ A): IO[A] = IO(() ⇒ a)

    implicit val programIO: Program[IO] = new Program[IO] {
      override def finish[A](a: ⇒ A): IO[A] = IO.point(a)

      override def chain[A, B](fa: IO[A], afb: A ⇒ IO[B]): IO[B] =
        fa.flatMap(afb)

      override def map[A, B](fa: IO[A], ab: A ⇒ B): IO[B] = fa.map(ab)
    }

    implicit val ConsoleIO: Console[IO] = new Console[IO] {
      override def printString(s: String): IO[Unit] = IO(() ⇒ println(s))

      override def getString: IO[String] = IO(() ⇒ readLine())
    }

    implicit val randomIO: Random[IO] = new Random[IO] {
      override def generateRandomNumber(num: Int): IO[Int] =
        IO(() ⇒ scala.util.Random.nextInt(num))
    }
  }

  def printString[F[_]: Console](str: String): F[Unit] =
    Console[F].printString(str)
  def getString[F[_]: Console]: F[String] = Console[F].getString

  def generateRandomNumber[F[_]: Random](num: Int): F[Int] =
    Random[F].generateRandomNumber(num)

  def checkRepeat[F[_]: Program: Console](name: String): F[Boolean] =
    for {
      _ ← printString("Do you want to continue, " + name + "?")
      input ← getString map (_.toLowerCase)
      cont ← input match {
        case "y" ⇒ point(true)
        case "n" ⇒ point(false)
        case _   ⇒ checkRepeat(name)
      }
    } yield cont

  def gameLoop[F[_]: Program: Console: Random](name: String): F[Unit] =
    for {
      num ← generateRandomNumber(5) map (_ + 1)
      _ ← printString("Dear " + name + ", please guess a number from 1 to 5:")
      input ← getString
      _ ← parseInt(input) match {
        case None ⇒
          printString(
            "You did not enter a valid number .Please input a valid number"
          )
        case Some(value) ⇒
          if (value == num) printString("You guessed right, " + name + "!")
          else
            printString(
              "You guessed wrong, " + name + "! The number was: " + num
            )
      }
      cont ← checkRepeat(name)
      _ ← if (cont) gameLoop(name) else point(())
    } yield ()

  def main[F[_]: Program: Console: Random]: F[Unit] =
    printString("What is your name?") flatMap { _ ⇒
      getString flatMap { name ⇒
        printString("Hello, " + name + ", welcome to the game!") flatMap { _ ⇒
          gameLoop(name)
        }
      }
    }
  main[IO].runUnreliableCode()
}
