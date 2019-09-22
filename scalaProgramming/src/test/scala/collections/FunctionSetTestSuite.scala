package com.home.testing

import org.scalatest.{ FunSuite, Matchers }
import com.home.collections.FunctionSet

class FunctionSetTestSuite extends FunSuite with Matchers {
  /**
    * Tests to check the empty set funcionality
    */
  test("Checking an elemnt in an empty set should return false") {
    FunctionSet.empty(randomString) shouldBe false
  }

  /**
    * Tests to check the add functionality
    */
  test("Taking the added element should return the true if it is in the set") {
    val first = randomString
    val second = randomString
    val third = randomString

    val set = FunctionSet.empty.add(first).add(second)

    set(first) shouldBe true
    set(second) shouldBe true
    set(third) shouldBe false
  }

  /**
    * Test to check the remove functionality
    */
  test("Check the remove function with various use cases") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val completeSet = FunctionSet.empty.add(first).add(second).add(third)
    val firstRemovedSet = completeSet.remove(first)
    val secondRemovedSet = completeSet.remove(second)
    val firstAndSecondRemovedSet = completeSet.remove(first).remove(second)

    completeSet(first) shouldBe true
    completeSet(second) shouldBe true
    completeSet(third) shouldBe true

    firstRemovedSet(first) shouldBe false
    firstRemovedSet(second) shouldBe true
    firstRemovedSet(third) shouldBe true

    secondRemovedSet(first) shouldBe true
    secondRemovedSet(second) shouldBe false
    secondRemovedSet(third) shouldBe true

    firstAndSecondRemovedSet(first) shouldBe false
    firstAndSecondRemovedSet(second) shouldBe false
    firstAndSecondRemovedSet(third) shouldBe true

    completeSet(fourth) shouldBe false
    firstRemovedSet(fourth) shouldBe false
    secondRemovedSet(fourth) shouldBe false
    firstAndSecondRemovedSet(fourth) shouldBe false
  }

  /**
    * Test to check the union functionality
    */
  test("Various test to check the union between sets") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val completeSet = FunctionSet.empty.add(first).add(second)
    val anotherSet = FunctionSet.empty.add(third)

    val nonEmptyUnionizedSet = completeSet.union(anotherSet)

    nonEmptyUnionizedSet(first) shouldBe true
    nonEmptyUnionizedSet(second) shouldBe true
    nonEmptyUnionizedSet(third) shouldBe true
    nonEmptyUnionizedSet(fourth) shouldBe false

    val emptySet = FunctionSet.empty
    val emptywithCompleteSet = emptySet.union(completeSet)

    emptywithCompleteSet(first) shouldBe true
    emptywithCompleteSet(second) shouldBe true
    emptywithCompleteSet(third) shouldBe false
  }

  /**
    * Test to check the intersections functionality
    */
  test("Various test to check the intersection between sets") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val completeSet = FunctionSet.empty.add(first).add(second)
    val anotherSet = FunctionSet.empty.add(third).add(first)

    val nonEmptyIntersectionzedSet = completeSet.intersection(anotherSet)

    nonEmptyIntersectionzedSet(first) shouldBe true
    nonEmptyIntersectionzedSet(second) shouldBe false
    nonEmptyIntersectionzedSet(third) shouldBe false
    nonEmptyIntersectionzedSet(fourth) shouldBe false

    val emptySet = FunctionSet.empty
    val emptywithCompleteSet = emptySet.intersection(completeSet)

    emptywithCompleteSet(first) shouldBe false
    emptywithCompleteSet(second) shouldBe false
    emptywithCompleteSet(third) shouldBe false
  }

  /**
    * Test to check the difference functionality
    */
  test("Various test to check the difference between sets") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val completeSet = FunctionSet.empty.add(first).add(second)
    val anotherSet = FunctionSet.empty.add(third).add(first)

    val nonEmptyDifferentionizedSet = completeSet.difference(anotherSet)

    nonEmptyDifferentionizedSet(first) shouldBe false
    nonEmptyDifferentionizedSet(second) shouldBe true
    nonEmptyDifferentionizedSet(third) shouldBe false
    nonEmptyDifferentionizedSet(fourth) shouldBe false

    val emptySet = FunctionSet.empty
    val emptywithCompleteSet = emptySet.difference(completeSet)

    emptywithCompleteSet(first) shouldBe false
    emptywithCompleteSet(second) shouldBe false
    emptywithCompleteSet(third) shouldBe false

    val completeSetWithEmptySet = completeSet.difference(emptySet)

    completeSetWithEmptySet(first) shouldBe true
    completeSetWithEmptySet(second) shouldBe true
    completeSetWithEmptySet(third) shouldBe false

  }
  def randomString: String = scala.util.Random.alphanumeric.take(8).mkString
}
