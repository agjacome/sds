package es.uvigo.ei.sing.sds
package service

import scala.annotation.tailrec
import scala.concurrent.Future

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._

final case class DrugEntity (
  term:  String,
  norm:  String,
  start: Int,
  end:   Int
) {

  def toAnnotation(articleId: Article.ID, keywordId: Keyword.ID): Annotation =
    Annotation(None, articleId, keywordId, term, start.toLong, end.toLong)

}

final class DrugsService {

  import DrugsService._

  def getEntities(text: String): Future[Set[DrugEntity]] =
    Future(drugs.toStream flatMap { case (term, norm) =>
      findAll(term, text).toStream.map(m => DrugEntity(term, norm, m._1, m._2))
    }).map(_.toSet)

  def normalize(entity: DrugEntity): Future[String] =
    Future.successful(entity.norm)

}

object DrugsService {

  import java.nio.file.{ Files, Path }
  import java.nio.charset.StandardCharsets.UTF_8

  import scala.collection.JavaConverters._

  private def dictionary: Path = dataDir.resolve("drugs").resolve("dict.tsv")

  lazy val drugs: Map[String, String] =
    Files.readAllLines(dictionary, UTF_8).asScala.map(_ split "\t" match {
      case Array(term, canonical) => (term -> canonical)
    }).toMap

}
