package com.home.collections

trait List[+A] {
  import List._
  def add[Super >: A](elem: Super): List[Super]
  def count: Int
  def map[Super >: A, B](f: Super => B): List[B]
  def fold[Super >: A, ACC](acc: ACC)(f: (ACC, Super) => ACC): ACC
  def ::[Super >: A](elem: Super): List[Super]
  //  def toString[Super >: A]: String = this match {
  //    case NonEmpty(elem, otherElems: NonEmpty[Super]) => s"List(${elem}, ${otherElems.element})"
  //    case NonEmpty(elem, otherElems: List.Empty)      => s"List(${elem})"
  //    case a: List.Empty                               => ""
  //  }
}

object List {
  final def apply[A](element1: A, otherElements: A*): List[A] = {
    val firstElement: List[A] = NonEmpty(element1, new Empty)
    //    val finalList = otherElements.foldRight(firstElement)((elem, acc) => acc.add(elem))
    val finalList = otherElements.foldLeft(firstElement)((acc, elem) => acc.add(elem))
    finalList
  }

  final def empty = new Empty
  //  def empty[A] :List.Empty.type = Empty.type

  //  implicit class consOperatorImplicitMethod[A](element: A) {
  //    def `::`(elemToAdd: A): List[A]= List.empty.add(elemToAdd)
  //  }
}
case class NonEmpty[+A](element: A, otherElems: List[A]) extends List[A] {
  final override def add[Super >: A](elem: Super): List[Super] = NonEmpty(elem, this)

  final override def count: Int = 1 + otherElems.count

  final override def map[Super >: A, B](f: Super => B): List[B] = {
    val mappedFirstElem = f(element)
    val nonEmptyFirstElem = NonEmpty(mappedFirstElem, otherElems.map(f))
    nonEmptyFirstElem
  }

  final override def fold[Super >: A, ACC](acc: ACC)(f: (ACC, Super) => ACC): ACC = {
    val functionAppliedToThisElem = f(acc, element)
    otherElems.fold(functionAppliedToThisElem)(f)
  }
  final override def toString: String = {
    //       val otherElemsToString = otherElems.
    s"List(${otherElems.fold(s"$element")((acc, elem) => s"$acc, $elem")})"
  }
  //    override def toString: String = super.toString
  final override def ::[Super >: A](elem: Super): List[Super] = this.add(elem)

}

class Empty extends List[Nothing] {
  final override def add[A](elem: A): List[A] = NonEmpty(elem, new Empty)

  final override def count: Int = 0

  final override def map[A, B](f: A => B): List[B] = new Empty

  final override def fold[Super >: Nothing, ACC](acc: ACC)(f: (ACC, Super) => ACC): ACC = acc

  final override def toString: String = "List()"
  //    override def toString: String = super.toString

  final override def ::[A](elem: A): List[A] = this.add(elem)
}

case object Nil extends Empty

