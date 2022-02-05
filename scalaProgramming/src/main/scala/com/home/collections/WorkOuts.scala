package com.home.collections

object WorkOuts extends App {
  trait Example {
    def a = println("in object Example")
  }

  def someFunction(a: Example) = println("in someFunction")
}