import freekzhomework.collections._

import org.scalatest._

class FuncitonSetTestSuite extends FunSuite with Matchers {
  test("Fetching an element from an empty set should return false") {
    FunctionSet.empty(randomString) shouldBe false
  }

  test("add on an empty set should return a set with a new element") {
    val first = randomString
    val second = randomString
    first should not be second

    val resSet = FunctionSet.empty.add(first)

    resSet(first) shouldBe true
  }
  test("add on a non empty set should yield a set with the new element") {
    val first = randomString
    val second = randomString

    val set = FunctionSet.empty.add(first).add(second)

    set(second) shouldBe true
    set(first) shouldBe true
  }
  test("Check the functionality of remove") {
    val first = randomString
    val second = randomString
    first should not be second

    val resSet = FunctionSet.empty.add(first).add(second).remove(first)

    resSet(first) shouldBe false
    resSet(second) shouldBe true
  }
  test("check the union functionality") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString
    val fifth = randomString

    val set1 = FunctionSet.empty.add(first).add(second)
    val set2 = FunctionSet.empty.add(third).add(fourth)

    val unionedSet = set1.union(set2)
    unionedSet(first) shouldBe true
    unionedSet(second) shouldBe true
    unionedSet(third) shouldBe true
    unionedSet(fourth) shouldBe true
    unionedSet(fifth) shouldBe false
  }

  test("check the intersection functionality") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fifth = randomString

    val set1 = FunctionSet.empty.add(first).add(second)
    val set2 = FunctionSet.empty.add(third).add(second)

    val intersection = set1.intersection(set2)
    intersection(first) shouldBe false
    intersection(second) shouldBe true
    intersection(third) shouldBe false
    intersection(fifth) shouldBe false
  }

  test("intersection on an empty set should return an empty set") {
    val first = randomString
    val second = randomString

    val emptySet = FunctionSet.empty
    val set1 = FunctionSet.empty.add(first).add(second)

    val intersection = set1.intersection(emptySet)
    intersection(first) shouldBe false
    intersection(second) shouldBe false
  }

  test("check the functionality of difference") {
    val first = randomString
    val second = randomString
    val third = randomString
    val fourth = randomString

    val set1 = FunctionSet.empty.add(first).add(second)
    val set2 = FunctionSet.empty.add(third).add(second).add(fourth)

    val difference = set1.difference(set2)
    difference(first) shouldBe true
    difference(second) shouldBe false
    difference(third) shouldBe false
    difference(fourth) shouldBe false
  }
  def randomString: String =
    scala.util.Random.alphanumeric.take(5).mkString
}

