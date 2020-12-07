package FPMartinOdersky

import FPMartinOdersky.Polynomial._
import org.scalatest._

class polynomialTestSuite extends FunSuite with Matchers {
  test("adding two polynomials should be non empty") {
    val poly1 = Poly(1 -> 2.0, 3 -> 4.0, 5 -> 6.2)
    val poly2 = Poly(0 -> 3.0, 3 -> 7.0)
    poly1 + poly2 should not be ""
  }
  test("adding two polynomials should produce the coefficient of corresponding exponents summed up") {
    val poly1 = Poly(1 -> 2.0, 3 -> 4.0, 5 -> 6.2)
    val poly2 = Poly(0 -> 3.0, 3 -> 7.0)
    (poly1 + poly2).toString shouldBe "6.2x^5+11.0x^3+2.0x^1+3.0x^0"
  }
}