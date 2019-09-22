package com.home.collections

trait SetFLD2[B] extends (B => Boolean) {

  final override def apply(input: B): Boolean =
    fold(false)(_ || input == _)

  final override def toString: String = {
    val res = fold(s"SetFLD2(") { (acc: String, elem: B) =>
      acc + elem.toString + ", "
    }
    res.replaceAll("..$", "") + ")"
  }

  final override def equals(input: Any): Boolean =
    input match {
      case emptySet: SetFLD2.Empty[B] => this.isEmpty
      case nonEmptySet: SetFLD2.NonEmpty[B] =>
        fold(false)(_ || nonEmptySet(_))
      case _ => false
    }

  final def add(input: B): SetFLD2[B] =
    fold(SetFLD2.NonEmpty[B](input, empty)) { (acc, elem: B) =>
      if (input != elem)
        SetFLD2.NonEmpty[B](elem, acc)
      else acc
    }

  final def remove(input: B): SetFLD2[B] =
    fold(empty[B]) { (acc: SetFLD2[B], elem: B) =>
      if (input != elem)
        SetFLD2.NonEmpty[B](elem, acc)
      else
        acc
    }

  final def size =
    fold(0) { (acc: Int, _) =>
      acc + 1
    }

  final def union(anotherSetFLD2: SetFLD2[B]): SetFLD2[B] =
    fold(anotherSetFLD2)(_ add _)

  final def intersection(anotherSetFLD2: SetFLD2[B]): SetFLD2[B] =
    fold(SetFLD2.empty[B]) { (acc, elem: B) =>
      if (anotherSetFLD2(elem))
        acc.add(elem)
      else
        acc
    }

  final def isSubSetOf(anotherSetFLD2: SetFLD2[B]): Boolean =
    fold(true)(_ && anotherSetFLD2(_))

  final def isSingleton: Boolean = {
    if (isEmpty) false
    else {
      val result = this.asInstanceOf[SetFLD2.NonEmpty[B]]
      if (result.otherElements.isEmpty) true
      else false
    }
  }

  final def isEmpty: Boolean = this.isInstanceOf[SetFLD2.Empty[B]]

  final def isNonEmpty: Boolean = !isEmpty

  final def empty[B]: SetFLD2[B] = SetFLD2.empty[B]

  final def foreach[A](function: B => A): Unit =
    fold(()) { (_, element) =>
      function(element)
    }

  final def sample: Option[B] = {
    var result: Option[B] = None
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetFLD2.NonEmpty[B]]
      result = Some(thisSet.element)
    }
    result
  }

  final def map[A](function: B => A): SetFLD2[A] =
    fold(empty[A])(_ add function(_))

  final def flatMap[A](function: B => SetFLD2[A]): SetFLD2[A] =
    fold(empty[A]) { (acc: SetFLD2[A], element: B) =>
      function(element).fold(acc)(_ add _)
    }

  final def filter(function: B => Boolean): SetFLD2[B] = {
    fold(empty[B]) { (acc: SetFLD2[B], elem: B) =>
      if (function(elem)) acc.add(elem)
      else acc
    }
  }

  @scala.annotation.tailrec
  final def fold[A](init: A)(function: (A, B) => A): A = {
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetFLD2.NonEmpty[B]]
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
object SetFLD2 {

  final class Empty[B] extends SetFLD2[B]

  final case class NonEmpty[B](element: B, otherElements: SetFLD2[B]) extends SetFLD2[B]

  /*Below are methods of companion object */
  final def empty[B]: SetFLD2[B] = new Empty[B]

  /*
  Below are the apply methods for the companion object SetFLD2
 */
  final def apply[B](firstElement: B, inputs: B*): SetFLD2[B] =
    inputs.foldLeft(empty[B].add(firstElement))(_ add _)

  final def apply[B](inputs: Seq[B]): SetFLD2[B] =
    inputs.foldLeft(empty[B])(_ add _)

}