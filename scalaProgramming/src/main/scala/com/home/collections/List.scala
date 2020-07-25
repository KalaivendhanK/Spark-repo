package com.home.collections

trait List[+A] {
  import List._
  def add[Super >: A](elem: Super): List[Super]
  def count: Int
  def map[Super >: A, B](f: Super => B): List[B]
  def fold[Super>: A, ACC](acc: ACC)(f: (ACC, Super ) => ACC) : ACC
//  def toString[Super >: A]: String = this match {
//    case NonEmpty(elem, otherElems: NonEmpty[Super]) => s"List(${elem}, ${otherElems.element})"
//    case NonEmpty(elem, otherElems: List.Empty)      => s"List(${elem})"
//    case a: List.Empty                               => ""
//  }
}

object List {
  def apply[A](element1: A, otherElements: A*): List[A] = {
    val firstElement: List[A] = NonEmpty(element1, new Empty)
    val finalList = otherElements.foldLeft(firstElement)((acc, elem) => acc.add(elem))
    finalList
  }

  case class NonEmpty[+A](element: A, otherElems: List[A]) extends List[A] {
    override def add[Super >: A](elem: Super): List[Super] = NonEmpty(elem, this)

    override def count: Int = 1 + otherElems.count

    override def map[Super >: A, B](f: Super => B): List[B] = {
      val mappedFirstElem = f(element)
      val nonEmptyFirstElem = NonEmpty(mappedFirstElem, otherElems.map(f))
      nonEmptyFirstElem
    }

    override def fold[Super >: A, ACC](acc: ACC)(f: (ACC, Super) => ACC): ACC ={
     val functionAppliedToThisElem = f(acc, element)
     otherElems.fold(functionAppliedToThisElem)(f)
    }
      override def toString: String = {
//       val otherElemsToString = otherElems.
        s"List($element, ${otherElems.fold("")((acc,elem) => s"$acc, $elem")})"
      }
//    override def toString: String = super.toString
  }

  class Empty extends List[Nothing] {
    override def add[A](elem: A): List[A] = NonEmpty(elem, new Empty)

    override def count: Int = 0

    override def map[A, B](f: A => B): List[B] = new Empty

    override def fold[Super >: Nothing, ACC](acc: ACC)(f: (ACC, Super) => ACC): ACC = acc

      override def toString: String = "List()"
//    override def toString: String = super.toString
  }

  //  def empty[A] :List.Empty.type = Empty.type

}
