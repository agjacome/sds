package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.concurrent.{ Future, future }

import abner.Tagger

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

private[annotator] class ABNERAdapter extends NERAdapter {

  private case class Entity(str : String, cat : Category, start : Position, end : Position)

  import context._
  import db.profile.simple._
  import ABNERAdapter._

  private[this] val Keywords    = KeywordsDAO()
  private[this] val Annotations = AnnotationsDAO()

  override protected def annotate(document : Document) : Future[Finished] = {
    getEntities(document.text) map { entities =>
      entities foreach { saveEntity(_, document) }
      Finished(document)
    }
  }

  private[this] def getEntities(text : String) : Future[Seq[Entity]] =
    future { abner getEntities text } map {
      case Array(entities, categories) => createEntities(
        entities zip (categories map toCategory), text
      )
    }

  private[this] def toCategory(str : String) : Category =
    Category(str filter (_.isLetterOrDigit))

  private[this] def createEntities(entities : Seq[(String, Category)], text : String) : Seq[Entity] = {
    @tailrec def iter(seq : Seq[(String, Category)], curr : Position, acc : Seq[Entity]) : Seq[Entity] =
      if (seq.isEmpty) acc else {
        val (str, cat)   = seq.head
        val (start, end) = getStartEnd(str, text, curr)
        iter(seq.tail, end, acc :+ Entity(str, cat, start, end))
      }

    iter(entities, 0, Seq.empty)
  }

  private[this] def getStartEnd(str : String, txt : String, from : Position = 0) : (Position, Position) = {
    val start = txt.indexOf(str, from.toInt)
    (start, start + str.length)
  }

  private[this] def saveEntity(entity : Entity, document : Document) : Unit = {
    val keyword    = getKeyword(entity)
    val annotation = getAnnotation(entity, keyword, document)
    insertAnnotation(keyword, annotation)
  }

  private[this] def getKeyword(entity : Entity) : Keyword =
    (Keywords findByNormalized entity.str) getOrElse (
      Keywords save Keyword(None, entity.str, entity.cat)
    )

  private[this] def getAnnotation(e : Entity, k : Keyword, d : Document) : Annotation =
    Annotation(None, d.id.get, k.id.get, e.str, e.start, e.end)

  private[this] def insertAnnotation(keyword : Keyword, annotation : Annotation) : Unit = {
    val current = (Keywords findById keyword.id).get
    Annotations save annotation
    Keywords save (current copy (occurrences = current.occurrences + 1))
  }

}

object ABNERAdapter {

  import java.io.File

  private lazy val abner : Tagger = new Tagger(
    new File(getClass.getResource("/abner/nlpba.crf").getFile)
  )

}

