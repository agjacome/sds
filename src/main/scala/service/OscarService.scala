package es.uvigo.ei.sing.sds
package service

import scala.collection.JavaConversions._
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import uk.ac.cam.ch.wwmm.oscar.Oscar
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.{ FormatType, ResolvedNamedEntity }

final class OscarService {

  import OscarService._

  def getNamedEntities(text: String): Future[Set[ResolvedNamedEntity]] =
    Future { oscar.findResolvableEntities(text).toSet }

  def normalize(entity: ResolvedNamedEntity): Future[String] =
    Future { entity.getFirstChemicalStructure(FormatType.STD_INCHI).getValue }

}

object OscarService {

  lazy val oscar = new Oscar()

  implicit class NamedEntityOps(rne: ResolvedNamedEntity) {
    import entity._
    def toAnnotation(articleId: Article.ID, keywordId: Keyword.ID): Annotation =
      Annotation(None, articleId, keywordId, rne.getSurface, rne.getStart.toLong, rne.getEnd.toLong)
  }

}
