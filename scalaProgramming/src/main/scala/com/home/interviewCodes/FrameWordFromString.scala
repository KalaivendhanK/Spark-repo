package com.home.interviewCodes

object FrameWordFromString extends App {

  val word = "appaisawesome"
  val sentence = "its an apple"
//  for {
//    w <- word
//    s <- sentence
//  } yield {
//    if (w != s ) false
//    else if (w != s ) false
//  }
//  canBeFramed(word, sentence)
  word.map((x:Char) => (x,1)).groupBy(x => x._1).
    map{
    case(c,lst) => (c,lst.map(_._2).toList.sum)
  }.foreach(println)
//  "aString".
}
