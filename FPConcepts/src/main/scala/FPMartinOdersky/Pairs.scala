package FPMartinOdersky

// TODO : Revisit the logic for isPrime Function
object Pairs extends App {
  val n = 10
  val primePairsGenerator = (1 until n) flatMap { i ⇒
    (1 until i) map { j ⇒
      (i, j)
    }
  } filter { x ⇒
    isPrime(x)
  }

  def isPrime(tuple: (Int, Int)): Boolean = {
    val (x, y) = tuple
    var result: Boolean = true
    (2 until x + y) foreach { n ⇒
      if (((x + y) / n) == 0) result = false else result = true
    }
    result
  }

  print(primePairsGenerator)
}
