package com.home.testing

import org.scalatest.{ FunSuite, Matchers }
import com.home.collections.SetC

class SetCTestSuite extends FunSuite with Matchers {
  private def set = SetC
  /**
    * Tests to check the empty set funcionality
    */
  test("Checking an elemnt in an empty set should return false") {
    set.empty shouldBe a[SetC]
  }

  /**
    * Test to check the add functionality on emptysets
    */
  test("adding an element on an empty set should produce a NonEmpty set") {
    val first = randomString
    val second = randomString
    set.empty.add(first) shouldBe a[SetC]
    set.empty.add(first) shouldBe a[SetC.NonEmpty]

    val setWithOneElement = set.empty.add(first)
    setWithOneElement(first) shouldBe true
    setWithOneElement(second) shouldBe false

    val setWithTwoElements = set.empty.add(first).add(second)
    setWithTwoElements(first) shouldBe true
    setWithTwoElements(second) shouldBe true
  }

  /**
    * Test to check the remove functionality on emptysets
    */
  test("remove on an empty set should produce an Empty set") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    set.empty.remove(first).size shouldBe 0

    val removedOneElement = set.empty.add(first).remove(first)
    removedOneElement(first) shouldBe false
    removedOneElement(second) shouldBe false

    val removedTwoElements = set.empty.add(first).add(second).remove(first)
    removedTwoElements(first) shouldBe false
    removedTwoElements(second) shouldBe true

    val setToCheckTheNullElements = set.add(first).add(second).add(second).add(third).add(fourth)
    // setToCheckTheNullElements(null) shouldBe false
    setToCheckTheNullElements.size shouldBe 4

  }
  /**
    * Test to check the duplicate handling in SetC
    */
  test("Duplicate elements should not exists in Sets") {
    val first = randomString
    val second = randomString

    val setWithDuplicates = set.empty.add(first).add(first).add(second)
    setWithDuplicates.size shouldBe 2
  }

  test("calling empty on a NonEmpty set should yield an empty Set") {
    val first = randomString
    val second = randomString

    val nonEmptySet = set.add(first).add(second)
    val nonEmptyTurnedEmpty = nonEmptySet.empty

    nonEmptySet(first) shouldBe true
    nonEmptySet(second) shouldBe true
    nonEmptyTurnedEmpty(first) shouldBe false
    nonEmptyTurnedEmpty(second) shouldBe false
    nonEmptyTurnedEmpty.size shouldBe 0
  }

  test("""Check the set constructor : Calling Set("1","2","3") should yield set with the three elemets""") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    SetC(first, second, third) shouldBe a[SetC.NonEmpty]

    val validSet = SetC(first, second, third, first, fourth)
    validSet(first) shouldBe true
    validSet(second) shouldBe true
    validSet(third) shouldBe true
    validSet.size shouldBe 4

  }

  /**
    * Tests to check the functionality of Union between Sets
    */
  test("Tests on union functionality ") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val unionedSet1 = SetC(first, second).union(SetC(first, third))
    val unionedSet2 = SetC(first, second).union(SetC(fourth))

    unionedSet1.size shouldBe 3
    unionedSet1(second) shouldBe true
    unionedSet1(fourth) shouldBe false
    unionedSet2(third) shouldBe false
    unionedSet2(first) shouldBe true
  }

  /**
    * Tests to check the functionality of Intersections between Sets
    */
  test("Tests on Intersection functionality") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val intersectSets1 = SetC(first, second).intersection(SetC(first, third))
    val intersectSets2 = SetC(first, second).intersection(SetC(fourth))

    intersectSets1.size shouldBe 1
    intersectSets1(first) shouldBe true
    intersectSets1(second) shouldBe false
    intersectSets1(fourth) shouldBe false
    intersectSets2(third) shouldBe false
    intersectSets2(first) shouldBe false
  }

  /**
    * Test to check the funcionality of isSubset
    */
  test("isSubset of an empty set should always be true") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val emptySet = SetC.empty
    val validSet = SetC(first, second)

    emptySet.isSubSetOf(validSet) shouldBe true
  }
  test("isSubset of an valid set should return true if all values of set 1 are in set 2") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val set1 = SetC(first)
    val set2 = SetC(first, second)

    set1.isSubSetOf(set2) shouldBe true
    set2.isSubSetOf(set1) shouldBe false

  }

  /**
    * Tests to check the functionality of isEmpty and isNonEmpty
    */
  test("isEmpty and isNonEmpty should pose opposite results") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val emptySet = SetC.empty
    emptySet.isEmpty shouldBe true
    emptySet.isNonEmpty shouldBe false

    val validSet = SetC(first, second)
    validSet.isEmpty shouldBe false
    validSet.isNonEmpty shouldBe true

    val setTurnedEmpty = validSet.remove(first).remove(second)
    setTurnedEmpty.isEmpty shouldBe true
  }

  /**
    * Test to check the singleton functionality
    */
  test("Check the functionality of singleton") {
    val first = randomString
    val second = randomString

    val someSet = SetC(first)
    someSet.isSingleton shouldBe true

    val emptySet = SetC.empty
    emptySet.isSingleton shouldBe false

    val validSet = SetC(first, second)
    validSet.isSingleton shouldBe false
    validSet.remove(first).isSingleton shouldBe true
    validSet.remove(first).remove(second).isSingleton shouldBe false

  }

  def randomString: String = scala.util.Random.alphanumeric.take(5).mkString
}