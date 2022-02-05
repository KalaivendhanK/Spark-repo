package com.home.LeetCode

import scala.util.control.Breaks.break

object TwoList extends App{
  object Solution {
    def twoSum(nums: Array[Int], target: Int): Array[Int] = {
      import scala.collection.mutable.HashMap
      val hashMap = new HashMap[Int,Int]()
      val res = new Array[Int](2)
      for (i <- 0 to nums.length - 1) {
        val diff = target - nums(i)
//        hashMap.foreach(println)
        println(s"nums: ${nums(i)}")
        if (i == 0)
          hashMap.put(diff, i)
        else {
          if(hashMap.contains(nums(i))) {
            res(0) = hashMap(nums(i))
            res(1) = i
          }
          else hashMap.put(diff, i)
        }
      }
      res
    }
  }
  val o1 = Solution.twoSum(Array(2,7,11,15), 0)
  o1.foreach(println)
}
