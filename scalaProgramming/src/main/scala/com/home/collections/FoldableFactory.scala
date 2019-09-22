package com.home.collections

import scala.language.higherKinds

trait FoldableFactory[+B, ST[+B] <: FoldableFactory[B, ST]] extends Foldable[B] {

  def empty[B]: ST[B]

  def add[S >: B](input: S): ST[S]

  def remove[S >: B](input: S): ST[S]

  def union[S >: B](anotherSetG: ST[S]): ST[S] =
    fold(anotherSetG)(_ add _)

  def intersection[S >: B](anotherSetG: ST[S]): ST[S]

  def map[A](function: B => A): ST[A] =
    fold(empty[A])(_ add function(_))

  def flatMap[A](function: B => Foldable[A]): ST[A] =
    fold(empty[A]) { (acc: ST[A], element: B) =>
      function(element).fold(acc)(_ add _)
    }

  def filter(function: B => Boolean): ST[B] = {
    fold(empty[B]) { (acc: ST[B], elem: B) =>
      if (function(elem))
        acc.add(elem)
      else acc
    }
  }

}