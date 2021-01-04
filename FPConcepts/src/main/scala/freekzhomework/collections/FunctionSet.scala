package freekzhomework.collections

trait FunctionSet extends (String ⇒ Boolean) {
  final def add(input: String): FunctionSet =
    (element: String) ⇒ input == element || this(element)

  def remove(input: String): FunctionSet =
    (element: String) ⇒
      input != element && this(element)

  def union(input: FunctionSet): FunctionSet =
    (element: String) ⇒ this(element) || input(element)

  def intersection(input: FunctionSet): FunctionSet =
    (element: String) ⇒ this(element) && input(element)

  def difference(input: FunctionSet): FunctionSet =
    (element: String) ⇒ this(element) && !input(element)
}

object FunctionSet {
  val empty: FunctionSet = (input: String) ⇒ false
}

