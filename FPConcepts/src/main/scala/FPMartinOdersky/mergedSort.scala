package FPMartinOdersky
object mergedSort extends App {

  def msort[T](x1: List[T])(predicate: (T, T) => Boolean): List[T] = {
    val n = x1.length / 2
    println(
      s"value in msort = $x1,length of list = ${x1.length}, value of n = $n")
    n match {
      case 0 => x1
      case _ => {
        val (part1, part2) = x1 splitAt n
        // println(s"splitted parts (part1, part2) = ($part1, $part2)")
        def merge(xs: List[T], ys: List[T]): List[T] = {
          println(s"values inside merge (part1, part2) = ($xs,$ys)")
          (xs, ys) match {
            case (Nil, ys) => ys
            case (xs, Nil) => xs
            case (x1 :: xrem, y1 :: yrem) =>
              if (predicate(x1, y1)) {
                val xres = x1 :: merge(xrem, ys)
                println(s"xres = $xres")
                xres
              } else {
                val yres = y1 :: merge(xs, yrem)
                println(s"yres = $yres")
                yres
              }
          }
        }

        merge(msort(part1)(predicate), msort(part2)(predicate))
        // merge(part1, part2)
      }
    }
  }
  val numbers = msort(List(5, 3, 6, 2, 4))(_ < _)
  //val strings = msort(List("oranges", "apples", "mangos"))(_ < _)
  val strings = msort(
    x1 = List("oranges", "apples", "mangos"))(
      predicate = (x, y) => x.compareTo(y) < 0)
  println("*" * 50)
  println(numbers)
  println(strings)
  println("*" * 50)
}
