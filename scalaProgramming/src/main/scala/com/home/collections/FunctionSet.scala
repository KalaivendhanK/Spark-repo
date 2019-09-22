package com.home.collections

trait FunctionSet extends (String => Boolean) {
  def add(input: String): FunctionSet = elem =>
    input == elem || this(elem)

  def remove(input: String): FunctionSet = elem =>
    input != elem && this(elem)

  def union(that: FunctionSet): FunctionSet = elem =>
    that(elem) || this(elem)

  def intersection(that: FunctionSet): FunctionSet = elem =>
    that(elem) && this(elem)

  def difference(that: FunctionSet): FunctionSet = elem =>
    this(elem) && !that(elem)

}

object FunctionSet {
  def empty: FunctionSet = _ => false

}