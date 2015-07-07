package es.uvigo.ei.sing.sds
package service

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.util.control.NonFatal

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import abner.Tagger

import entity._

final case class ABNEREntity (
  txt:   String,
  cat:   Category,
  start: Int,
  end:   Int
) {

  def toAnnotation(articleId: Article.ID, keywordId: Keyword.ID): Annotation =
    Annotation(None, articleId, keywordId, txt, start.toLong, end.toLong)

}

final class ABNERService {

  import ABNERService._

  def getEntities(text: String): Future[Set[ABNEREntity]] =
    Future(abner.getEntities(text)) map {
      case Array(es, cs) => toEntities(es.zip(cs.map(toCategory)), text)
    } recover {
      case NonFatal(e) => Logger.info("Hidden ABNER error:", e); Set.empty
    }

  def normalize(entity: ABNEREntity): Future[String] =
    Future.successful(entity.txt.toLowerCase)

  private def toCategory(str: String): Category =
    Category(str.filter(_.isLetterOrDigit)).getOrError(s"Invalid ABNER Category '$str'")

  // TODO: delete explicit recursivity, can be done with a foldLeft
  private def toEntities(entities: Seq[(String, Category)], text: String): Set[ABNEREntity] = {
    @tailrec def iter(xs: Seq[(String, Category)], pos: Int, acc: Set[ABNEREntity]): Set[ABNEREntity] =
      if (xs.isEmpty) acc else {
        println(xs.head)
        val (term, category) = xs.head
        findPositions(term, text, pos.toInt) match {
          case Some((start, end)) => iter(xs.tail, end, acc + ABNEREntity(term, category, start, end))
          case None               => iter(xs.tail, pos + term.length, acc)
        }
      }

    iter(entities, 0, Set.empty)
  }

  private def findPositions(str: String, txt: String, from: Int = 0): Option[(Int, Int)] =
    Option(txt.indexOf(str, from)).filter(_ > 0).map(i => (i, i + str.length))

}

object ABNERService {

  lazy val abner: Tagger = new Tagger

}
