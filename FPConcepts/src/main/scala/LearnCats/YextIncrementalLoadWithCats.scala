package LearnCats

import cats.syntax._
import cats.implicits._

trait Monoid1[T] {
  def combine(a: T, b: T): T
  def empty(a: T): T
}
object Monoid1 {
  def apply[T](implicit M: Monoid1[T]): Monoid1[T] = M
}
object Monoid1Instances {
  implicit val MonoidInstancesForInts: Monoid1[Int] = new Monoid1[Int] {
    override def combine(a: Int, b: Int): Int = a + b

    override def empty(a: Int): Int = 0
  }

}

object YextIncrementalLoadWithCats extends App {

  import Monoid1Instances._
  val integer1 = 2
  val integer2 = 3

  val result = Monoid1[Int].combine(integer1, integer2)
  print(result)

}
