package es.uvigo.ei.sing.sds
package service

import scala.collection.JavaConversions._
import scala.concurrent.Future

import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import uk.ac.man.entitytagger.Mention

import entity._

final class LinnaeusService {

  import LinnaeusService._

  lazy val eUtils = new EUtilsService

  def getMentions(text: String): Future[Set[Mention]] =
    Future { linnaeus.`match`(text).toSet }

  def normalize(mention: Mention): Future[String] = {
    val id = mention.getMostProbableID.split(":").last.toLong
    scientificNameOf(id).map(_.getOrElse(s"NCBI Taxonomy ID: $id"))
  }

  private def scientificNameOf(taxonomyId: Long): Future[Option[String]] =
    Cache.getAs[String](s"taxonomy-id($taxonomyId)").fold({
      val name = eUtils.fetchTaxonomyScientificName(taxonomyId)
      name.foreach(n => Cache.set(s"taxonomy-id($taxonomyId)", n))
      name
    })(name => Future.successful(Some(name)))

}

object LinnaeusService {

  import java.nio.file.Files
  import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
  import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

  private def dictionary  = Files.newInputStream(dataDir.resolve("linnaeus").resolve("dict-species.tsv"))
  private def frequencies = Files.newInputStream(dataDir.resolve("linnaeus").resolve("freq-species.tsv"))
  private def stopList    = Files.newInputStream(dataDir.resolve("linnaeus").resolve("stoplist.tsv"))
  private def synonyms    = Files.newInputStream(dataDir.resolve("linnaeus").resolve("synonyms.tsv"))

  lazy val linnaeus: Matcher = new MatchPostProcessor(
    VariantDictionaryMatcher.load(dictionary, true),
    Matcher.Disambiguation.ON_WHOLE,
    true,
    null,
    new Postprocessor(Array(stopList), Array(synonyms), Array(frequencies), null, null)
  )

  implicit class MentionOps(mention: Mention) {
    def toAnnotation(articleId: Article.ID, keywordId: Keyword.ID): Annotation =
      Annotation(None, articleId, keywordId, mention.getText, mention.getStart.toLong, mention.getEnd.toLong)
  }

}
