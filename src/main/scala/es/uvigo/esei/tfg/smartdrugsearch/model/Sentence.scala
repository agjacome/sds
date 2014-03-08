package es.uvigo.esei.tfg.smartdrugsearch.model

final class Sentence private (val words : Seq[String]) {

  require(words forall (!_.isEmpty), "Sentences must be nonempty")

  override lazy val toString : String  = words mkString " "
  override lazy val hashCode : Int     = words.hashCode

  override def equals(other : Any) : Boolean =
    other match {
      case that : Sentence => words == that.words
      case _               => false
    }

}

object Sentence extends (String => Sentence) {

  import scala.language.implicitConversions

  lazy val Empty = new Sentence(List())

  def apply(words : String) : Sentence =
    new Sentence(words.trim.toLowerCase split ("\\s+"))

  implicit def stringToSentence(words : String) : Sentence = Sentence(words)
  implicit def sentenceToString(words : Sentence) : String = words.toString

}

