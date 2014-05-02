package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.annotation.tailrec
import scala.concurrent.future

import abner.Tagger

import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[annotator] class ABNERAdapter extends NERAdapter {

  import context._
  import ABNERAdapter._

  private case class Entity(str : String, cat : Category, start : Position, end : Position)

  override protected def annotate(document : Document) =
    getEntities(document.text) map { entities =>
      entities foreach { saveEntity(_, document) }
      Finished(document)
    }

  private[this] def getEntities(text : String) =
    future { abner getEntities text } map {
      case Array(entities, categories) => createEntities(entities zip (categories map toCategory), text)
    }

  private[this] def toCategory(str : String) =
    Category(str filter (_.isLetterOrDigit))

  private[this] def createEntities(entities : Seq[(String, Category)], text : String) = {
    @tailrec def iter(seq : Seq[(String, Category)], curr : Position, acc : Seq[Entity]) : Seq[Entity] =
      if (seq.isEmpty) acc else {
        val (str, cat)   = seq.head
        val (start, end) = getStartAndEnd(str, text, curr)
        iter(seq.tail, end, acc :+ Entity(str, cat, start, end))
      }

    iter(entities, 0, Seq.empty)
  }

  private[this] def getStartAndEnd(str : String, txt : String, from : Position = 0) = {
    val start = txt.indexOf(str, from.toInt)
    (start, start + str.length)
  }

  private[this] def saveEntity(entity : Entity, document : Document) = {
    val keyword    = getOrStoreNewKeyword(entity.str, entity.cat)
    val annotation = getAnnotation(entity, keyword, document)
    storeAnnotation(keyword, annotation)
  }

  private[this] def getAnnotation(e : Entity, k : Keyword, d : Document) =
    Annotation(None, d.id.get, k.id.get, e.str, e.start, e.end)

}

private object ABNERAdapter {

  import java.io.File

  private lazy val abner = new Tagger(
    new File(getClass.getResource("/abner/nlpba.crf").getFile)
  )

}

