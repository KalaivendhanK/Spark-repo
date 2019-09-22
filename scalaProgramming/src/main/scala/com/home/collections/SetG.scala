package com.home.collections

trait SetG[+B] extends FoldableFactory[B, SetG] {

  final def apply[S >: B](input: S): Boolean =
    fold(false)(_ || input == _)

  @scala.annotation.tailrec
  final override def fold[A](init: A)(function: (A, B) => A): A = {
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetG.NonEmpty[B]]
      val element = thisSet.element
      val otherElements = thisSet.otherElements

      val value = function(init, element)
      otherElements.fold(value)(function)
    }
    else init
  }

  final override def toString: String = {
    val res = fold(s"SetG(") { (acc: String, elem: B) =>
      acc + elem.toString + ", "
    }
    res.replaceAll("..$", "") + ")"
  }

  final override def equals(input: Any): Boolean =
    input match {
      case emptySet: SetG.Empty[B] => this.isEmpty
      case nonEmptySet: SetG.NonEmpty[B] =>
        fold(false)(_ || nonEmptySet(_))
      case _ => false
    }

  final override def add[S >: B](input: S): SetG[S] =
    fold(SetG.NonEmpty[S](input, empty)) { (acc, elem: S) =>
      if (input != elem)
        SetG.NonEmpty[S](elem, acc)
      else acc
    }

  final override def remove[S >: B](input: S): SetG[S] =
    fold(empty[S]) { (acc: SetG[S], elem: S) =>
      if (input != elem)
        SetG.NonEmpty[S](elem, acc)
      else
        acc
    }

  final override def intersection[S >: B](anotherSetG: SetG[S]): SetG[S] =
    fold(empty[S]) { (acc, elem: S) =>
      if (anotherSetG(elem))
        acc.add(elem)
      else
        acc
    }

  final def isSubSetOf[S >: B](anotherSetG: SetG[S]): Boolean =
    fold(true)(_ && anotherSetG(_))

  final def isEmpty: Boolean = this.isInstanceOf[SetG.Empty[B]]

  final def isNonEmpty: Boolean = !isEmpty

  final override def empty[B]: SetG[B] = SetG.empty[B]

  final def sample: Option[B] = {
    var result: Option[B] = None
    if (!isEmpty) {
      val thisSet = this.asInstanceOf[SetG.NonEmpty[B]]
      result = Some(thisSet.element)
    }
    result
  }

}

/**
  *  Companion object definition starts here
  */
object SetG extends Factory[SetG] {

  final class Empty[B] extends SetG[B]

  final case class NonEmpty[B](element: B, otherElements: SetG[B]) extends SetG[B]

  /*Below are methods of companion object */
  final override def empty[B]: SetG[B] = new Empty[B]

  final implicit def newFunction[B](SetG: SetG[B]): B => Boolean =
    SetG.apply
}