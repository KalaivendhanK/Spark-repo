package com.home.collections

trait SetFETyped[B] extends (B => Boolean) {

  final override def apply(input: B): Boolean = {
    var result = false
    foreach { elem: B => result = result || input == elem
    }
    result
  }

  final def add1(input: B): SetFETyped[B] = {
    if (isEmpty) SetFETyped.NonEmpty[B](input, this)
    else {
      var result = this.asInstanceOf[SetFETyped.NonEmpty[B]]
      if (input != result.element && !result.otherElements(input))
        result = SetFETyped.NonEmpty[B](input, this)

      result
    }
  }

  final def add(input: B): SetFETyped[B] = {
    var result: SetFETyped[B] = SetFETyped.NonEmpty[B](input, empty)
    foreach { elem: B =>
      if (input != elem)
        result = SetFETyped.NonEmpty[B](elem, result)
    }
    result
  }

  final def remove1(input: B): SetFETyped[B] = {
    if (isEmpty) SetFETyped.empty[B]
    else {
      var result = this.asInstanceOf[SetFETyped.NonEmpty[B]]
      if (input != result.element && !result.otherElements(input))
        result = SetFETyped.NonEmpty[B](input, this)

      result
    }
  }

  final def remove(input: B): SetFETyped[B] = {
    var result: SetFETyped[B] = empty[B]
    foreach { elem: B =>
      if (input != elem)
        result = SetFETyped.NonEmpty[B](elem, result)
    }
    result
  }

  final def size = {
    var result = 0
    foreach { elem: B =>
      result = result + 1
    }
    result
  }

  final def union(anotherSetFETyped: SetFETyped[B]): SetFETyped[B] = {
    var result: SetFETyped[B] = anotherSetFETyped
    foreach { elem: B =>
      result = result.add(elem)
    }
    result
  }

  final def intersection(anotherSetFETyped: SetFETyped[B]): SetFETyped[B] = {
    var result: SetFETyped[B] = SetFETyped.empty[B]
    foreach { elem: B =>
      if (anotherSetFETyped(elem))
        result = result.add(elem)
    }
    result
  }

  final def isSubSetOf(anotherSetFETyped: SetFETyped[B]): Boolean = {
    var result = true
    foreach { elem: B =>
      result = result && anotherSetFETyped(elem)
    }
    result
  }

  final def isSingleton: Boolean = {
    if (isEmpty) false
    else {
      val result = this.asInstanceOf[SetFETyped.NonEmpty[B]]
      if (result.otherElements.isEmpty) true
      else false
    }
  }

  final def isEmpty: Boolean = this.isInstanceOf[SetFETyped.Empty[B]]

  final def isNonEmpty: Boolean = !isEmpty

  final def empty[B]: SetFETyped[B] = SetFETyped.empty[B]

  final def foreach[A](function: B => A): Unit = {
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetFETyped.NonEmpty[B]]
      function(thisSet.element)
      thisSet.otherElements.foreach(function)
    }
    else ()
  }

  final def sample: Option[B] = {
    var result: Option[B] = None
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetFETyped.NonEmpty[B]]
      result = Some(thisSet.element)
    }
    result
  }

  final def map[A](function: B => A): SetFETyped[A] = {
    var result = empty[A]
    if (isNonEmpty) {
      foreach { elem =>
        result = result.add(function(elem))
      }
    }
    result
  }

  final def flatMap[A](function: B => SetFETyped[A]): SetFETyped[A] = {
    var result: SetFETyped[A] = empty[A]
    foreach { elem: B =>
      function(elem).foreach {
        x => result = result.add(x)
      }
    }
    result
  }

  @scala.annotation.tailrec
  final def fold[A](init: A)(function: (A, B) => A): A = {
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetFETyped.NonEmpty[B]]
      val element = thisSet.element
      val otherElements = thisSet.otherElements

      val value = function(init, element)
      otherElements.fold(value)(function)
    }
    else init

  }

}

/**
  *  Companion object definition starts here
  */
object SetFETyped {
  /**
    * Class definition for Empty type Set
    * @type Empty
    */
  final class Empty[B] extends SetFETyped[B]
  // object Empty {
  //   def apply[B](): Empty[B] = new Empty[B]
  // }

  /**
    * Class definition for NonEmpty type Sets
    * @type NonEmpty
    */
  final case class NonEmpty[B](element: B, otherElements: SetFETyped[B]) extends SetFETyped[B]

  /*Below are methods of companion object */
  final def empty[B]: SetFETyped[B] = new Empty[B]

  /*
  Below are the apply methods for the companion object SetFETyped
 */
  final def apply[B](firstElement: B, inputs: B*): SetFETyped[B] = {
    var initialSet = empty[B].add(firstElement)
    inputs foreach { element: B =>
      initialSet = initialSet.add(element)
    }
    initialSet
  }

  final def apply[B](inputs: Seq[B]): SetFETyped[B] = {
    var initialSet = empty[B]
    inputs foreach { element: B =>
      initialSet = initialSet.add(element)
    }
    initialSet
  }

}