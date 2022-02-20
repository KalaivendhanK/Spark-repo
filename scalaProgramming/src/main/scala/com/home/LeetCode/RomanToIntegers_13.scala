package com.home.LeetCode

object RomanToIntegers_13 extends App {
  object Solution {
    def romanToInt(str: String): Int = {
      val s = str.toUpperCase()
      val romanMap = Map[Char,Int]('I' -> 1, 'V' -> 5, 'X' -> 10,
      'L' -> 50, 'C' -> 100, 'D' -> 500, 'M' -> 1000)

      val reducerMap = Map[Char, String]('I' -> "VX", 'X' -> "LC", 'C' -> "DM")

      var sum = 0
      if(s.length == 1) return romanMap(s(0))
      for (i <- s.length-1 to 0 by -1){
        println(s"Current letter in process: ${s(i)}")
        println(s"""Current letter : ${s(i)} Next letter: ${if( i != s.length -1) s(i+1) else ""}""")
        if( i != s.length-1
         && reducerMap.keys.toArray.contains(s(i))
       ) {
         val nextRoman = s(i+1)
          if(reducerMap(s(i)).toArray.contains(nextRoman)) {
           sum = sum - romanMap(s(i))
           println(s"Inside reducer")
         }
          else
            sum = sum + romanMap(s(i))
          println(s"Inside sub adder")
       }
       else {
          println(s"Inside adder")
          sum = sum + romanMap(s(i))
        }
        println(s"sum: ${sum}")

      }
      sum
    }
  }

  println(Solution.romanToInt("MCMXCIV"))
}
