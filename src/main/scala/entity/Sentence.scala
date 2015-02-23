package es.uvigo.esei.tfg.smartdrugsearch.entity

final class Sentence private (val words : Seq[String]) {

  require(words forall (!_.isEmpty), "Sentences must be nonempty")

  override lazy val toString = mkString(" ")
  override lazy val hashCode = (words map (_.toLowerCase)).hashCode()

  def mkString(separator : String) : String =
    words mkString separator

  override def equals(other : Any) : Boolean =
    other match {
      case that : Sentence => (words map (_.toLowerCase)) == (that.words map (_.toLowerCase))
      case _               => false
    }

}

object Sentence extends (String => Sentence) {

  import scala.language.implicitConversions
  import play.api.libs.json._
  import play.api.mvc.{ PathBindable, QueryStringBindable }

  lazy val Empty = new Sentence(List())

  def apply(words : String) : Sentence =
    new Sentence(words.trim split "\\s+")

  implicit def stringToSentence(words : String) : Sentence = Sentence(words)
  implicit def sentenceToString(words : Sentence) : String = words.toString

  implicit val sentenceWrites = Writes { (s : Sentence) => JsString(s.toString) }
  implicit val sentenceReads  = Reads.of[String] map apply

  implicit def bindPath(implicit binder : PathBindable[String]) : PathBindable[Sentence] =
    binder transform (apply, _.toString)

  implicit def bindQuery : QueryStringBindable[Sentence] =
    QueryStringBindable.bindableString transform (apply, _.toString)

}

