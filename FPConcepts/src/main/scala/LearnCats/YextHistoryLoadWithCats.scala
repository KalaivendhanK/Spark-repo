package LearnCats

import simulacrum._
import cats.{ Semigroup, Monad }
import cats.implicits._

object YextHistoryLoadWithCats {
  @typeclass trait Monad[F[_]] {
    def lift[A](a: A): F[A]
    @op("<->") def map[A, B](fa: F[A])(f: A => B): F[B]
    @op("<<->>") def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  }
  //    implicit class ExtensionForIOMOnads[A](ioa: IO[A]){
  //      def lift(a: A)(implicit IOM: Monad[IO]): IO[A] = IOM.lift(a)
  //      def map[B](f: A => B)(implicit IOM: Monad[IO]):IO[B] = IOM.map(ioa)(f)
  //      def flatMap[B](f: A => IO[B])(implicit  IOM: Monad[IO]): IO[B] = IOM.flatMap(ioa)(f)
  //    }

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
  case class IO[A](run: () => A)

  object Monad {
    implicit val monadForIO: Monad[IO] = new Monad[IO] {
      override def lift[A](a: A): IO[A] = IO(() => a)
      override def map[A, B](fa: IO[A])(f: A => B): IO[B] =
        IO(() => f(fa.run()))
      override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
        IO(() => f(fa.run()).run())
    }
  }

  def main: Unit = {
    val yextEndpoint = "https://liveapi.yext.com/v2/accounts/me/entities?api_key=dbaf2f4bfa0b0e2da6417ed815706ae5&v=20200601&&limit=1&offset=1&entityTypes=location"

  }
//  Semigroup[Int].combine(1, 2)

}
