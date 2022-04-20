package com.home.HackerRank

/** Given a square matrix, calculate the absolute difference between the sums of its diagonals.
  *
  * For example, the square matrix  is shown below:
  *
  *  1 2 3
  *  4 5 6
  *  9 8 9
  *  The left-to-right diagonal = . The right to left diagonal = . Their absolute difference is .
  *
  *  Function description:
  *  Complete the  function in the editor below.
  *
  *  diagonalDifference takes the following parameter:
  *  int arr[n][m]: an array of integers
  *
  *  Return
  *  int: the absolute diagonal difference
  *
  *  Input Format:
  *  The first line contains a single integer, , the number of rows and columns in the square matrix .
  *  Each of the next  lines describes a row, , and consists of  space-separated integers .
  *
  *  Output Format
  *  Return the absolute difference between the sums of the matrix's two diagonals as a single integer.
  *
  *  Sample Input
  *
  *  11 2 4
  *  4 5 6
  *  10 8 -12
  *
  *  Sample Output
  *  15
  *
  *  Explanation
  *
  *  The primary diagonal is:
  *  11
  *     5
  *       -12
  *  Sum across the primary diagonal: 11 + 5 - 12 = 4
  *
  *  The secondary diagonal is:
  *
  *       4
  *     5
  *  10
  *
  *  Sum across the secondary diagonal: 4 + 5 + 10 = 19
  *  Difference: |4 - 19| = 15
  */

object SumOfDiagonalsIn2DArray extends App {
  object Solution {
    /*
     * Complete the 'diagonalDifference' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts 2D_INTEGER_ARRAY arr as parameter.
     */

    def diagonalDifference(arr: Array[Array[Int]]): Int = {
      // Write your code here
      if (arr(0).length != arr.length) return 0

      var i               = 0
      var j               = 0
      var normal_diag_sum = 0
      while (i < arr.length) {
        while (j < arr.length) {
          if (i == j) normal_diag_sum = normal_diag_sum + arr(i)(j)
          j = j + 1
        }
        i = i + 1
      }
      println(s"Normal diag sum $normal_diag_sum")

      val secondArray: Array[Array[Int]] = arr.map(inn => inn.reverse)

      var k                = 0
      var l                = 0
      var reverse_diag_sum = 0

      while (k < secondArray.length) {
        while (l < secondArray.length) {
          if (k == l) reverse_diag_sum += secondArray(k)(l)
          l += 1
        }
        k += 1
      }

      println(s"Reversed diag sum $reverse_diag_sum")
      val totalSum: Int    = normal_diag_sum - reverse_diag_sum
      val finalResult: Int = if (totalSum > 0) totalSum else totalSum * -1
      println(finalResult)
      finalResult

    }
  }

  val testArr: Array[Array[Int]] = Array(Array(1, 2, 3), Array(4, 5, 6), Array(9, 8, 9))
  testArr.foreach(x => println(x.mkString))
  val expected: Int              = 15
  val actual: Int                = Solution.diagonalDifference(testArr)
  if (expected == actual) println("correct") else ("Wrong")
}
