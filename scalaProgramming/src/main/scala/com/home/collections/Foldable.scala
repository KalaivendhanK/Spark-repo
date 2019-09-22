package com.home.collections

trait Foldable[+B] {
  def fold[A](init: A)(function: (A, B) => A): A

  def foreach[A](function: B => A): Unit =
    fold(()) { (_, element) =>
      function(element)
    }

  def size: Int =
    fold(0) { (acc: Int, _) =>
      acc + 1
    }

  final def isSingleton: Boolean =
    size == 1

  def contains[S >: B](input: S): Boolean =
    fold(false)(_ || input == _)

  final def notContains[S >: B](input: S): Boolean =
    !contains(input)
}