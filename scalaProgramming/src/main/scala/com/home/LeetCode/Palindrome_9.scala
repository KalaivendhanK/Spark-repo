package com.home.LeetCode

object Palindrome_9 extends App {
  object Solution {
    def isPalindrome(x: Int): Boolean = {
      val xString: Array[Char] = x.toString.toArray
      var i: Int = 0
      var j: Int = xString.length - 1
      while (i <= j) {
        if (xString(i)!= xString(j)) return false
        i+=1
        j-=1
      }
     true
    }
  }
  object Solution2 {
    def helper(arrString: Array[Char], i: Int,j: Int): Boolean = {
      if(i==j) true
      else if ( arrString(i) != arrString(j)) false
      else helper(arrString, i+1, j-1)
    }
    def isPalindrome(x: Int): Boolean = {
      val xString: Array[Char] = x.toString.toArray
      val i = 0
      val j = xString.length - 1

      helper(xString,i,j)
    }
  }
  object Solution3 {
    def helper(arrString: Array[Char], i: Int,j: Int): Boolean = {
      if(i==j) true
      else if ( arrString(i) != arrString(j)) false
      else helper(arrString, i+1, j-1)
    }
    def isPalindrome(x: Int): Boolean = {
      val xString: Array[Char] = x.toString.toArray
      val i = 0
      val j = xString.length - 1

      helper(xString,i,j)
    }
  }
  println(Solution2.isPalindrome(1200121))
  // another way to implement
  val testString = 12321.toString
  println(testString == testString.reverse)
}
