package com.home.LeetCode

object TwoSum_13 extends App {
  object Solution {
    def romanToInt(str: String): Int = {
      val s = str.toUpperCase()
      val romanMap = Map[Char,Int]('I' -> 1, 'V' -> 5, 'X' -> 10,
      'L' -> 50, 'C' -> 100, 'D' -> 500, 'M' -> 1000)


      var sum = 0
      if(s.length == 1) return romanMap(s(0))
      for (i <- 0 to s.length-1){
       if( i == 0 && Array[Char]('C','X','I').contains(s(i)) && s(i) != s(i + 1)) {
        sum = sum - romanMap(s(i))
       }
       else sum = sum + romanMap(s(i))
      }
      sum
    }
  }

  println(Solution.romanToInt("XXXIV"))
}
