package com.home.testing

import org.scalatest.{ FunSuite, Matchers }
import com.home.collections.SetFE

class SetFETestSuite extends FunSuite with Matchers {
  test("check the functionality of foreach") {
    val a = randomString
    val b = randomString
    val c = randomString

    var accum1: Int = 0
    var accum2: Int = 0

    // val validSet = SetFE(a, b, c).foreach(elem => accum1 = accum1 + elem.length)
    // val twoElementSet = SetFE(a, b, c).remove(b).foreach(elem => accum2 = accum2 + elem.length)

    // accum1 shouldBe 15
    // accum2 shouldBe 10

    val elementSet = SetFE(a, b).add(c).add(b)
    elementSet(c) shouldBe true

    elementSet.size shouldBe 3
  }

  test("apply on the set should be able to return the appropriate results") {
    val a = randomString
    val b = randomString
    val c = randomString

    val validSet = SetFE(a, b)
    validSet(a) shouldBe true

    val (elements, set) = setMaker(10)

    set(elements(0)) shouldBe true
    set(elements(1)) shouldBe true
    set(elements(2)) shouldBe true

    set.size shouldBe 10
  }

  test("test the union for the foreach implementation") {
    val a = randomString
    val b = randomString
    val c = randomString
    val d = randomString
    val e = randomString

    val validSet1 = SetFE(a, b)
    val validSet2 = SetFE(c, d)

    val unionizedSet = validSet1.union(validSet2)

    unionizedSet(a) shouldBe true
    unionizedSet(b) shouldBe true
    unionizedSet(c) shouldBe true
    unionizedSet(d) shouldBe true
    unionizedSet(e) shouldBe false

    val unionWithRemove = unionizedSet.remove(a).remove(b)

    unionWithRemove(a) shouldBe false
    unionWithRemove(b) shouldBe false

    val removedUnionWithAdd = unionWithRemove.add(a)

    removedUnionWithAdd(a) shouldBe true
    removedUnionWithAdd(b) shouldBe false
  }

  test("test the intersection for the foreach implementation") {
    val a = randomString
    val b = randomString
    val c = randomString
    val d = randomString
    val e = randomString

    val validSet1 = SetFE(a, b, c)
    val validSet2 = SetFE(b, c, d)

    val intersectionizedSet = validSet1.intersection(validSet2)

    intersectionizedSet(a) shouldBe false
    intersectionizedSet(b) shouldBe true
    intersectionizedSet(c) shouldBe true
    intersectionizedSet(d) shouldBe false
    intersectionizedSet(e) shouldBe false

    val intersectionWithRemove = intersectionizedSet.remove(a).remove(b)

    intersectionWithRemove(a) shouldBe false
    intersectionWithRemove(b) shouldBe false

    val removedintersectionWithAdd = intersectionWithRemove.add(a)

    removedintersectionWithAdd(a) shouldBe true
    removedintersectionWithAdd(b) shouldBe false
  }

  test("test the is singleton functionality") {
    val a = randomString
    val b = randomString
    val c = randomString

    val validSet1 = SetFE(a, b, c)
    val validSet2 = SetFE(a)

    validSet1.isSingleton shouldBe false
    validSet2.isSingleton shouldBe true

    val convertedToSingleton = validSet1.remove(a).remove(c)

    convertedToSingleton.isSingleton shouldBe true
  }

  def randomString: String = scala.util.Random.alphanumeric.take(5).mkString
  def setMaker(elements: Int): (Seq[String], SetFE) = {
    val elems = (0 until elements).map(_ => randomString)
    elems -> SetFE(elems)
  }
}