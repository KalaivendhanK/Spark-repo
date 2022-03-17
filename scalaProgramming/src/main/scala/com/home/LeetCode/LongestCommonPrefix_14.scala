package com.home.LeetCode

object LongestCommonPrefix_14 {
  object Solution {
    def longestCommonPrefix(strs: Array[String]): String = {

      def allTheSame[A](input: Array[Option[A]]): Boolean = {
        val containsNone: Boolean = input.contains(None)
        if (containsNone) false else input.forall(input.headOption.contains)
      }


      def loop(index: Int, prefix: String): String = {

        val chars: Array[Option[Char]] = strs.map{ string =>
          scala.util.Try(string.charAt(index)).toOption
        }

        if (!allTheSame(chars)) prefix
        else {
          val firstChar: Any = strs.headOption.flatMap{ string =>
            scala.util.Try(string.charAt(index)).toOption
          }.getOrElse("")
          loop(index + 1, prefix + firstChar)
        }
      }
      if (strs.isEmpty) ""
      else loop(0, "")
    }
  }

}
