package FPMartinOdersky

object FrameSentences extends App {
  //  val dictionary = Source.fromURL("https://svnweb.freebsd.org/csrg/share/dict/words?view=co&content-type=text/plain")
  //  val words: Iterator[String] = dictionary.getLines()
  //  (words toSet ) take(10000) foreach println

  val manualWords: List[String] =
    List("Scala", "is", "fun", "and", "is", "really", "interesting", "to", "learn", "it", "keeps", "refreshing", "the", "thoughts", "functional", "programming", "is", "quite", "impressive")

  val words: List[String] = manualWords filter {
    _ forall (_.isLetter)
  }
  val dialpadMappings: Map[Char, String] = Map('2' -> "ABC", '3' -> "DEF", '4' -> "GHI", '5' -> "JKL",
    '6' -> "MNO", '7' -> "PQRS", '8' -> "TUV", '9' -> "WXYZ")

  //  convert the mapping of characters to numbers.
  val wordsWithNumbers: Map[Char, Char] =
    for {
      (digits, letters) <- dialpadMappings
      eachLetters <- letters
    } yield eachLetters -> digits

  //  creates the string of numbers for the given word.
  def numberForWord(word: String): String =
    word.toUpperCase() map wordsWithNumbers

  val wordsForNumber: Map[String, Seq[String]] =
    words groupBy numberForWord withDefaultValue Seq()

  def encode(number: String): Set[List[String]] =
    if (number.isEmpty) Set(List())
    else {
      for {
        split <- 1 to number.length
        word <- wordsForNumber(number take split)
        rest <- encode(number drop split)
      } yield word :: rest
    }.toSet

  def transform(number: String): Set[String] =
    encode(number) map {
      _ mkString " "
    }

  println(transform("7225247386"))
}
