package functionalProgrammingInScalaMartinOdersky

//Object for performing operation on polynomials
// 5x^3+2x^2-4x
object Polynomial {
  class Poly(terms: Map[Int, Double]) {
    // def this(values: (Int, Double)*) = this(values.toMap)
    val termsWithValue = terms withDefaultValue 0.0
    // def +(that: Poly): Poly = new Poly(this.terms ++ (that.termsWithValue map adjust))
    def +(that: Poly): Poly = new Poly((that.termsWithValue foldLeft termsWithValue)(addTerm))
    def addTerm(terms: Map[Int, Double], otherTerm: (Int, Double)): Map[Int, Double] = {
      val (exp, coeff) = otherTerm
      terms + (exp -> (coeff + terms(exp)))
    }
    override def toString(): String = (for ((expo, coeff) <- terms.toList.sorted.reverse) yield coeff + "x^" + expo) mkString "+"
    def adjust(term: (Int, Double)): (Int, Double) = {
      val (expo, coeff) = term
      expo -> (coeff + termsWithValue(expo))
    }
  }
  object Poly {
    def apply(values: (Int, Double)*) = new Poly(values.toMap)
  }
}