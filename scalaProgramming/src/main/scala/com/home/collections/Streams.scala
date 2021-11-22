package com.home.collections

object Streams extends App {
  val aStream = Stream.continually(1)

  /**
    * WARNING!!!
    * The below println will create an infinite stream of lines.
    * Don't execute unless necessary !!!
    */
//  println(aStream.fold(0)((count,_) => {println(s"count: $count");count + 1}))
}
