package com.home.collections

trait Factory[ST[+B] <: FoldableFactory[B, ST]] {

  def empty[A]: ST[A]

  def apply[B](firstElement: B, inputs: B*): ST[B] =
    inputs.foldLeft(empty[B].add(firstElement))(_ add _)

  def apply[B](inputs: Seq[B]): ST[B] =
    inputs.foldLeft(empty[B])(_ add _)
}