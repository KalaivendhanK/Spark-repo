package LearnCats

import simulacrum._
import cats._
object TheBegining {
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

  case class IO[A](run: () => A)

  object IO {
    implicit val ioForMonad: Monad[IO] = new Monad[IO] {
      override def lift[A](a: A): IO[A] = IO(() => a)
      override def map[A, B](fa: IO[A])(f: A => B): IO[B] =
        IO(() => f(fa.run()))
      override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
        IO(() => f(fa.run()).run())
    }
  }

}
