package functionalProgrammingInScalaMartinOdersky

object Queens extends App {
  def queens(n: Int): Set[List[Int]] = {
    def placeQueens(k: Int): Set[List[Int]] = {
      if (k == 0) Set(List())
      else
        for {
          queens <- placeQueens(k - 1)
          col <- 0 to n - 1
          if isSafe(col, queens)
        } yield col :: queens
    }
    def isSafe(col: Int, queens: List[Int]): Boolean = {
      val rows = queens.length
      val queenWithRows = (rows - 1 to 0 by -1) zip (queens)
      queenWithRows forall {
        case (r: Int, c: Int) => col != c && math.abs(col - c) != rows - r
      }
    }
    placeQueens(n)
  }
  def show(queens: List[Int]): String = {
    val length = queens.length
    val lines = for {
      queenPosition <- queens.reverse
    } yield Vector.fill(length)("* ").updated(queenPosition, "Q ").mkString
    "\n" + (lines mkString "\n")
  }
  print(queens(5) map show mkString "\n")
}
