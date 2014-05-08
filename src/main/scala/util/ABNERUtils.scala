package es.uvigo.esei.tfg.smartdrugsearch.util

import scala.annotation.tailrec

import abner.Tagger

import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Position, Category }

case class ABNEREntity(str : String, cat : Category, start : Position, end : Position)

class ABNERUtils private {

  import ABNERUtils._

  def getEntities(text : String) : Seq[ABNEREntity] =
    abner getEntities text match {
      case Array(entities, categories) => createEntities(entities zip (categories map toCategory), text)
    }

  // ABNER does not perform any normalization, so just return the entity string
  def normalize(entity : ABNEREntity) : String =
    entity.str

  private[this] def toCategory(str : String) =
    Category(str filter (_.isLetterOrDigit))

  private[this] def createEntities(entities : Seq[(String, Category)], text : String) = {
    @tailrec
    def iter(seq : Seq[(String, Category)], curr : Position, acc : Seq[ABNEREntity]) : Seq[ABNEREntity] =
      if (seq.isEmpty) acc else {
        val (str, cat)   = seq.head
        val (start, end) = getStartAndEnd(str, text, curr)
        iter(seq.tail, end, acc :+ ABNEREntity(str, cat, start, end))
      }

    iter(entities, 0, Seq.empty)
  }

  private[this] def getStartAndEnd(str : String, txt : String, from : Position = 0) = {
    val start = txt.indexOf(str, from.toInt)
    (start, start + str.length)
  }

}

object ABNERUtils extends (() => ABNERUtils) {

  import java.io.File

  lazy val abner = new Tagger(new File(getClass.getResource("/abner/nlpba.crf").getFile))

  def apply( ) : ABNERUtils =
    new ABNERUtils

}

