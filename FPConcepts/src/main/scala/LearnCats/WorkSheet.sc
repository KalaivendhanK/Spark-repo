
import cats._
//import cats.Monoid.sy
import cats.implicits._

def sum[A: Monoid](xs: List[A]): A = {
  val m = implicitly[Monoid[A]]
  xs.foldLeft(m.empty)(m.combine)

}

sum(List("a", "b", "c"))

List(1,2,3).foldMap(x => List((x,x.toString)))




