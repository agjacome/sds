package es.uvigo.esei.tfg.smartdrugsearch.entity

final class Sentence private (val words : Seq[String]) {

  require(words forall (!_.isEmpty), "Sentences must be nonempty")

  def mkString(separator : String) = words mkString separator

  override lazy val toString : String  = mkString(" ")
  override lazy val hashCode : Int     = (words map (_.toLowerCase)).hashCode

  override def equals(other : Any) : Boolean =
    other match {
      case that : Sentence => (words map (_.toLowerCase)) == (that.words map (_.toLowerCase))
      case _               => false
    }

}

object Sentence extends (String => Sentence) {

  import scala.language.implicitConversions

  lazy val Empty = new Sentence(List())

  def apply(words : String) : Sentence =
    new Sentence(words.trim split ("\\s+"))

  implicit def stringToSentence(words : String) : Sentence = Sentence(words)
  implicit def sentenceToString(words : Sentence) : String = words.toString

}

