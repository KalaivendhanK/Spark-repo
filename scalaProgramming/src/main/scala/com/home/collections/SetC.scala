package com.home.collections

trait SetC extends (String => Boolean) {
  def add(input: String): SetC

  def remove(input: String): SetC

  // def size(currentSize: Int): Int
  def size: Int

  def empty = SetC.Empty

  def union(anotherSetC: SetC): SetC

  def intersection(anotherSetC: SetC): SetC

  def isSubSetOf(anotherSetC: SetC): Boolean

  def isSingleton: Boolean

  def isEmpty: Boolean = this == SetC.empty

  final def isNonEmpty: Boolean = !isEmpty
}

object SetC {
  /**
    * Class definition for Empty type Set
    * @type Empty
    */
  final case object Empty extends SetC {
    override def apply(input: String): Boolean =
      false

    final override def add(input: String): SetC =
      NonEmpty(input, this)

    // final override def size(currentSize: Int = 0) = 0
    final override def size: Int = 0

    final override def remove(input: String): SetC =
      this

    final override def union(anotherSetC: SetC): SetC = anotherSetC

    final override def intersection(anotherSetC: SetC): SetC = this

    final override def isSubSetOf(anotherSetC: SetC): Boolean = true

    final override def isSingleton: Boolean = false

  }

  /**
    * Class definition for NonEmpty type Sets
    * @type NonEmpty
    */
  final /*private[this]*/ case class NonEmpty(element: String, otherElements: SetC) extends SetC {
    override def apply(input: String): Boolean =
      input == element || otherElements(input)

    final override def add(input: String): SetC =
      if (input == element || otherElements(input))
        this
      else
        NonEmpty(input, otherElements.add(element))

    final override def remove(input: String): SetC =
      if (input == element)
        otherElements
      else
        NonEmpty(element, otherElements.remove(input))

    final override def union(anotherSetC: SetC): SetC = {
      val newSet = anotherSetC.add(element)
      otherElements.union(newSet)
    }

    final override def intersection(anotherSetC: SetC): SetC = {
      if (anotherSetC(element))
        otherElements.intersection(anotherSetC).add(element)
      else
        otherElements.intersection(anotherSetC)
    }

    final override def isSubSetOf(anotherSetC: SetC): Boolean =
      anotherSetC(element) && otherElements.isSubSetOf(anotherSetC)

    /*
    final override def size(currentSize: Int = 0): Int = {
      var elementCount = 0
      if (otherElements.isInstanceOf[Empty.type])
        elementCount = currentSize + 1
      else if (otherElements.isInstanceOf[NonEmpty]) {
        elementCount = currentSize + 1
        elementCount = otherElements.size(currentSize = elementCount)
      }
      elementCount
    }

   */
    final override def size: Int = 1 + otherElements.size

    final override def isSingleton: Boolean = otherElements == empty
  }

  /*Below are methods of companion object */
  final def empty: SetC = Empty

  final def add(input: String): SetC =
    NonEmpty(input, SetC.Empty)

  final def apply(inputs: String*): SetC = {
    var initialSet: SetC = empty
    inputs foreach { element: String =>
      initialSet = initialSet.add(element)
    }
    initialSet
  }
}