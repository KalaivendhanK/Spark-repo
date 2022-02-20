package com.home.interviewCodes.FreshWorks

object ReverseArrayNTimes extends App {
  def rotate_array(r: Int, arrayElem: Array[Int]): Array[Int] = {
    val newArray = new Array[Int](arrayElem.length)
    arrayElem.copyToArray(newArray)
    if (r == 0) newArray
    else rotate_array(r - 1, newArray.reverse)

  }

  rotate_array(2, Array[Int](1, 4, 5, 2, 35, 6, 3)).foreach(println)

}
