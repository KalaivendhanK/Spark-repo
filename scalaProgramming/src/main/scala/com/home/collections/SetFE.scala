package com.home.collections

trait SetFE extends (String => Boolean) {

  final def apply(input: String): Boolean = {
    var result = false
    foreach { elem => result = result || input == elem
    }
    result
  }

  final def add1(input: String): SetFE = {
    if (isEmpty) SetFE.NonEmpty(input, this)
    else {
      var result = this.asInstanceOf[SetFE.NonEmpty]
      if (input != result.element && !result.otherElements(input))
        result = SetFE.NonEmpty(input, this)

      result
    }
  }

  final def add(input: String): SetFE = {
    var result: SetFE = SetFE.NonEmpty(input, empty)
    foreach { elem =>
      if (input != elem)
        result = SetFE.NonEmpty(elem, result)
    }
    result
  }

  final def remove1(input: String): SetFE = {
    if (isEmpty) SetFE.Empty
    else {
      var result = this.asInstanceOf[SetFE.NonEmpty]
      if (input != result.element && !result.otherElements(input))
        result = SetFE.NonEmpty(input, this)

      result
    }
  }

  final def remove(input: String): SetFE = {
    var result: SetFE = empty
    foreach { elem =>
      if (input != elem)
        result = SetFE.NonEmpty(elem, result)
    }
    result
  }

  final def size = {
    var result = 0
    foreach { elem =>
      result = result + 1
    }
    result
  }

  final def union(anotherSetFE: SetFE): SetFE = {
    var result: SetFE = anotherSetFE
    foreach { elem =>
      result = result.add(elem)
    }
    result
  }

  final def intersection(anotherSetFE: SetFE): SetFE = {
    var result: SetFE = SetFE.Empty
    foreach { elem =>
      if (anotherSetFE(elem))
        result = result.add(elem)
    }
    result
  }

  final def isSubSetOf(anotherSetFE: SetFE): Boolean = {
    var result = true
    foreach { elem =>
      result = result && anotherSetFE(elem)
    }
    result
  }

  final def isSingleton: Boolean = {
    if (isEmpty) false
    else {
      val result = this.asInstanceOf[SetFE.NonEmpty]
      if (result.otherElements.isEmpty) true
      else false
    }
  }

  def isEmpty: Boolean = this == SetFE.empty

  final def isNonEmpty: Boolean = !isEmpty

  def empty = SetFE.Empty

  final def foreach(function: String => Unit): Unit = {
    if (this != SetFE.Empty) {
      val thisSet = this.asInstanceOf[SetFE.NonEmpty]
      function(thisSet.element)
      thisSet.otherElements.foreach(function)
    }
  }

  // final def map[A](function: String => B): Set[B] = {
  //   if (!isEmpty) {
  //     foreach { elem =>
  //       function(elem)
  //     }
  //   }
  // }

}

object SetFE {
  /**
    * Class definition for Empty type Set
    * @type Empty
    */
  final case object Empty extends SetFE

  /**
    * Class definition for NonEmpty type Sets
    * @type NonEmpty
    */
  final /*private[this]*/ case class NonEmpty(element: String, otherElements: SetFE) extends SetFE

  /*Below are methods of companion object */
  final def empty: SetFE = Empty

  /*
  Below are the apply methods for the companion object SetFE
 */
  final def apply(firstElement: String, inputs: String*): SetFE = {
    var initialSet: SetFE = empty.add(firstElement)
    inputs foreach { element: String =>
      initialSet = initialSet.add(element)
    }
    initialSet
  }

  final def apply(inputs: Seq[String]): SetFE = {
    var initialSet: SetFE = empty
    inputs foreach { element: String =>
      initialSet = initialSet.add(element)
    }
    initialSet
  }

}