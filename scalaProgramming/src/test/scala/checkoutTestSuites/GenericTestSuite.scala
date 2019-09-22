package com.home.testing

import org.scalatest._
import com.home.testing._

class GenericTestSuite extends FunSuite with Matchers {
  test("pass as this is a simple test check of 1 == 1") {
    1 == 1
  }

  test("Check whether the count of the dataframe is 2") {
    TestData().testDS.count == 2
  }
}
