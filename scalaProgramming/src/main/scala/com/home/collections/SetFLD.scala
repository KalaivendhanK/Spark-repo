package com.home.collections

trait SetFLD[B] extends (B => Boolean) {

  final override def apply(input: B): Boolean =
    fold(false) { (acc: Boolean, elem: B) =>
      acc || input == elem
    }

  final override def toString: String =
    if (isEmpty)
      "SetFLD()"
    else {
      val res = fold(s"SetFLD(") { (acc: String, elem: B) =>
        acc + elem.toString + ", "
      }
      res.replaceAll("..$", "") + ")"
    }

  final def add(input: B): SetFLD[B] =
    fold(SetFLD.NonEmpty[B](input, empty)) { (acc, elem: B) =>
      if (input != elem)
        SetFLD.NonEmpty[B](elem, acc)
      else acc
    }

  final def remove(input: B): SetFLD[B] =
    fold(empty[B]) { (acc: SetFLD[B], elem: B) =>
      if (input != elem)
        SetFLD.NonEmpty[B](elem, acc)
      else
        acc
    }

  final def size =
    fold(0) { (acc: Int, _) =>
      acc + 1
    }

  final def union(anotherSetFLD: SetFLD[B]): SetFLD[B] =
    fold(anotherSetFLD) { (acc, elem: B) =>
      acc.add(elem)
    }

  final def intersection(anotherSetFLD: SetFLD[B]): SetFLD[B] =
    fold(SetFLD.empty[B]) { (acc, elem: B) =>
      if (anotherSetFLD(elem))
        acc.add(elem)
      else
        acc
    }

  final def isSubSetOf(anotherSetFLD: SetFLD[B]): Boolean =
    fold(true) { (acc: Boolean, elem: B) =>
      acc && anotherSetFLD(elem)
    }

  final def isSingleton: Boolean = {
    if (isEmpty) false
    else {
      val result = this.asInstanceOf[SetFLD.NonEmpty[B]]
      if (result.otherElements.isEmpty) true
      else false
    }
  }

  final def isEmpty: Boolean = this.isInstanceOf[SetFLD.Empty[B]]

  final def isNonEmpty: Boolean = !isEmpty

  final def empty[B]: SetFLD[B] = SetFLD.empty[B]

  final def foreach[A](function: B => A): Unit =
    fold(()) { (_, element) =>
      function(element)
    }

  final def sample: Option[B] = {
    var result: Option[B] = None
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetFLD.NonEmpty[B]]
      result = Some(thisSet.element)
    }
    result
  }

  final def map[A](function: B => A): SetFLD[A] = {
    fold(empty[A]) { (acc: SetFLD[A], element: B) =>
      {
        acc.add(function(element))
      }

    }
  }

  final def flatMap[A](function: B => SetFLD[A]): SetFLD[A] =
    fold(empty[A]) { (acc: SetFLD[A], element: B) =>
      function(element).fold(acc) { (innerAcc: SetFLD[A], innerElement: A) =>
        innerAcc.add(innerElement)
      }
    }

  @scala.annotation.tailrec
  final def fold[A](init: A)(function: (A, B) => A): A = {
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetFLD.NonEmpty[B]]
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
object SetFLD {

  final class Empty[B] extends SetFLD[B]

  final case class NonEmpty[B](element: B, otherElements: SetFLD[B]) extends SetFLD[B]

  /*Below are methods of companion object */
  final def empty[B]: SetFLD[B] = new Empty[B]

  /*
  Below are the apply methods for the companion object SetFLD
 */
  final def apply[B](firstElement: B, inputs: B*): SetFLD[B] =
    inputs.foldLeft(empty[B].add(firstElement)) { (acc: SetFLD[B], elem: B) =>
      acc.add(elem)
    }

  final def apply[B](inputs: Seq[B]): SetFLD[B] =
    inputs.foldLeft(empty[B]) { (acc: SetFLD[B], elem: B) =>
      acc.add(elem)
    }

}