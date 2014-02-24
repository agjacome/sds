package es.uvigo.esei.tfg.smartdrugsearch.model.ner

final class Sentence private (val words : Seq[String]) {

  require(!words.mkString.isEmpty, "Sentences must be nonempty")

  override lazy val toString = words mkString " "

  def countWords : Int =
    words.size

  override def equals(other : Any) : Boolean =
    other match {
      case that : Sentence => words == that.words
      case _               => false
    }

  override def hashCode : Int =
    words.hashCode

}

object Sentence extends (String => Sentence) {

  object Predef {

    import scala.language.implicitConversions

    implicit def stringToSentence(words : String) : Sentence =
      Sentence(words)

  }

  def apply(words : String) : Sentence =
    new Sentence(words.trim split ("\\s+"))

}

