package com.home.collections
// added comment
trait SetV[+B] /*extends (B => Boolean)*/ {

  final /*override*/ def apply[S >: B](input: S): Boolean =
    fold(false)(_ || input == _)

  final override def toString: String = {
    val res = fold(s"SetV(") { (acc: String, elem: B) =>
      acc + elem.toString + ", "
    }
    res.replaceAll("..$", "") + ")"
  }

  final override def equals(input: Any): Boolean =
    input match {
      case emptySet: SetV.Empty[B] => this.isEmpty
      case nonEmptySet: SetV.NonEmpty[B] =>
        fold(false)(_ || nonEmptySet(_))
      case _ => false
    }

  final def add[S >: B](input: S): SetV[S] =
    fold(SetV.NonEmpty[S](input, empty)) { (acc, elem: S) =>
      if (input != elem)
        SetV.NonEmpty[S](elem, acc)
      else acc
    }

  final def remove[S >: B](input: S): SetV[S] =
    fold(empty[S]) { (acc: SetV[S], elem: S) =>
      if (input != elem)
        SetV.NonEmpty[S](elem, acc)
      else
        acc
    }

  final def size =
    fold(0) { (acc: Int, _) =>
      acc + 1
    }

  final def union[S >: B](anotherSetV: SetV[S]): SetV[S] =
    fold(anotherSetV)(_ add _)

  final def intersection[S >: B](anotherSetV: SetV[S]): SetV[S] =
    fold(SetV.empty[S]) { (acc, elem: S) =>
      if (anotherSetV(elem))
        acc.add(elem)
      else
        acc
    }

  final def isSubSetOf[S >: B](anotherSetV: SetV[S]): Boolean =
    fold(true)(_ && anotherSetV(_))

  final def isSingleton: Boolean = {
    if (isEmpty) false
    else {
      val result = this.asInstanceOf[SetV.NonEmpty[B]]
      if (result.otherElements.isEmpty) true
      else false
    }
  }

  final def isEmpty: Boolean = this.isInstanceOf[SetV.Empty[B]]

  final def isNonEmpty: Boolean = !isEmpty

  final def empty[B]: SetV[B] = SetV.empty[B]

  final def foreach[A](function: B => A): Unit =
    fold(()) { (_, element) =>
      function(element)
    }

  final def sample: Option[B] = {
    var result: Option[B] = None
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetV.NonEmpty[B]]
      result = Some(thisSet.element)
    }
    result
  }

  final def map[A](function: B => A): SetV[A] =
    fold(empty[A])(_ add function(_))

  final def flatMap[A](function: B => SetV[A]): SetV[A] =
    fold(empty[A]) { (acc: SetV[A], element: B) =>
      function(element).fold(acc)(_ add _)
    }

  final def filter(function: B => Boolean): SetV[B] = {
    fold(empty[B]) { (acc: SetV[B], elem: B) =>
      if (function(elem))
        acc.add(elem)
      else acc
    }
  }

  @scala.annotation.tailrec
  final def fold[A](init: A)(function: (A, B) => A): A = {
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetV.NonEmpty[B]]
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
object SetV {

  final class Empty[B] extends SetV[B]

  final case class NonEmpty[B](element: B, otherElements: SetV[B]) extends SetV[B]

  /*Below are methods of companion object */
  final def empty[B]: SetV[B] = new Empty[B]

  /*
  Below are the apply methods for the companion object SetV
 */
  final def apply[B](firstElement: B, inputs: B*): SetV[B] =
    inputs.foldLeft(empty[B].add(firstElement))(_ add _)

  final def apply[B](inputs: Seq[B]): SetV[B] =
    inputs.foldLeft(empty[B])(_ add _)

  final implicit def newFunction[B](setV: SetV[B]): B => Boolean =
    setV.apply
}