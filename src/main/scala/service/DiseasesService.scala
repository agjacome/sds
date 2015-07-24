package es.uvigo.ei.sing.sds
package service

import scala.annotation.tailrec
import scala.concurrent.Future

import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import dispatch.{ as, url, Http }
import entity._

final case class DiseaseEntity (
  term:  String,
  doid:  String,
  start: Int,
  end:   Int
) {

  def toAnnotation(articleId: Article.ID, keywordId: Keyword.ID): Annotation =
    Annotation(None, articleId, keywordId, term, start.toLong, end.toLong)

}

final class DiseasesService {

  import DiseasesService._

  def getEntities(text: String): Future[Set[DiseaseEntity]] =
    Future(diseases.toStream flatMap { case (term, doid) =>
      findAll(term, text).toStream.map(m => DiseaseEntity(term, doid, m._1, m._2))
    }).map(_.toSet)

  def normalize(entity: DiseaseEntity): Future[String] =
    getCanonicalName(entity.doid).map(_.getOrElse(entity.doid))

  private def getCanonicalName(doid: String): Future[Option[String]] =
    Cache.getAs[String](s"disease-id($doid)").fold({
      val name = fetchNameFromOntology(doid)
      name.foreach(n => Cache.set(s"disease-id($doid)", n))
      name
    })(name => Future.successful(Some(name)))

  private def fetchNameFromOntology(doid: String): Future[Option[String]] =
    for {
      res  <- Http(url(s"http://www.disease-ontology.org/api/metadata/$doid/") > as.String)
      json <- Future.successful(Json.parse(res))
    } yield (json \ "name").get.asOpt[String]

}

object DiseasesService {

  import java.nio.file.{ Files, Path }
  import java.nio.charset.StandardCharsets.UTF_8

  import scala.collection.JavaConverters._

  private def dictionary: Path = dataDir.resolve("diseases").resolve("dict.tsv")

  lazy val diseases: Map[String, String] =
    Files.readAllLines(dictionary, UTF_8).asScala.map(_ split "\t" match {
      case Array(term, doid) => (term -> doid)
    }).toMap

}
