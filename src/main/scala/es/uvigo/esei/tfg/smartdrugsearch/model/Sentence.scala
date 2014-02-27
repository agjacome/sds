package es.uvigo.esei.tfg.smartdrugsearch.model

final class Sentence private (val words : Seq[String]) {

  require(words forall (!_.isEmpty), "Sentences must be nonempty")

  override lazy val toString : String = words mkString " "

  override lazy val hashCode : Int = words.hashCode

  override def equals(other : Any) : Boolean =
    other match {
      case that : Sentence => words == that.words
      case _               => false
    }

}

object Sentence extends (String => Sentence) {

  object Predef {

    import scala.language.implicitConversions
    import scala.slick.lifted.{ TypeMapper, MappedTypeMapper }

    implicit def stringToSentence(words : String)   : Sentence = Sentence(words)
    implicit def sentenceToString(words : Sentence) : String   = words.toString

    implicit val sentenceTypeMapper : TypeMapper[Sentence] =
      MappedTypeMapper.base[Sentence, String](sentenceToString, stringToSentence)

  }

  def apply(words : String) : Sentence =
    new Sentence(words.trim split ("\\s+"))

}

