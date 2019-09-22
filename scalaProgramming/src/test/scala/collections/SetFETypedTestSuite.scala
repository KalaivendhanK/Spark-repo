package com.home.testing

import org.scalatest.{ FunSuite, Matchers }
import com.home.collections.SetFETyped

class SetFETypedTestSuite extends FunSuite with Matchers {
  test("check the functionality of foreach") {
    val a = randomString
    val b = randomString
    val c = randomString

    var accum1: Int = 0
    var accum2: Int = 0

    val validSet = SetFETyped(a, b, c).foreach { elem: String =>
      accum1 += elem.length
    }
    val twoElementSet = SetFETyped(a, b, c).remove(b).foreach { elem: String =>
      accum2 += elem.length
    }

    accum1 shouldBe 15
    accum2 shouldBe 10

    val elementSet = SetFETyped(a, b).add(c).add(b)
    elementSet(c) shouldBe true

    elementSet.size shouldBe 3
  }
  test("apply on the set should be able to return the appropriate results") {
    val a = randomString
    val b = randomString
    val c = randomString

    val validSet = SetFETyped(a, b)
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

    val validSet1 = SetFETyped(a, b)
    val validSet2 = SetFETyped(c, d)

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

    val validSet1 = SetFETyped(a, b, c)
    val validSet2 = SetFETyped(b, c, d)

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

    val validSet1 = SetFETyped(a, b, c)
    val validSet2 = SetFETyped(a)

    validSet1.isSingleton shouldBe false
    validSet2.isSingleton shouldBe true

    val convertedToSingleton = validSet1.remove(a).remove(c)

    convertedToSingleton.isSingleton shouldBe true
  }

  test("test the map functionality") {
    val a = "three"
    val b = "one"
    val c = "four"

    val validSet1 = SetFETyped(a, b, c)

    val mappedSet1 = validSet1.map { (x: String) =>
      x.length
    }

    mappedSet1 shouldBe an[SetFETyped.NonEmpty[Int]]

    var acc: Int = 0
    val mappedSet2 = validSet1.map { (x: String) =>
      x.length
    }.foreach { (x: Int) =>
      acc += x
    }

    acc shouldBe 12
  }

  test("test to check the functionality of sample") {
    val a = "three"
    val b = "one"
    val c = "four"

    val validSet1 = SetFETyped(a, b, c)
    val validSet2 = SetFETyped.empty

    validSet1.sample.getOrElse("not found") should not be None
    validSet1.sample.getOrElse("not found").size > 0
    validSet2.sample.getOrElse("not found") shouldBe "not found"
  }

  test("test the functionality of flat map by making it to create a chessboard") {
    val x: SetFETyped[Char] = SetFETyped('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    val y: SetFETyped[Int] = SetFETyped(1, 2, 3, 4, 5, 6, 7, 8)

    val chessboard: SetFETyped[(Char, Int)] = x.flatMap { x =>
      y.map { y =>
        (x -> y)
      }
    }
    // chessboard.foreach(println)
    chessboard.size shouldBe 64

    val sampleChessboardValue = 'b' -> 5
    chessboard(sampleChessboardValue) shouldBe true
    chessboard(('z' -> 3)) shouldBe false

  }

  test("Check the functionality of fold") {
    val a = randomString
    val b = randomString
    val c = randomString

    val validSet1 = SetFETyped(a, b, c)

    // validSet1.fold()
  }
  def randomString: String = scala.util.Random.alphanumeric.take(5).mkString
  def setMaker(elements: Int): (Seq[String], SetFETyped[String]) = {
    val elems = (0 until elements).map(_ => randomString)
    elems -> SetFETyped(elems)
  }

}