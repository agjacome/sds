package es.uvigo.ei.sing.sds
package service

import scala.collection.JavaConversions._
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.cache.Cache
import play.api.Play.current

import uk.ac.man.entitytagger.Mention

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

  import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
  import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

  lazy val dictionary  = Thread.currentThread.getContextClassLoader.getResourceAsStream("/linnaeus/dict-species.tsv")
  lazy val frequencies = Thread.currentThread.getContextClassLoader.getResourceAsStream("/linnaeus/freq-species.tsv")
  lazy val stopList    = Thread.currentThread.getContextClassLoader.getResourceAsStream("/linnaeus/stoplist.tsv")
  lazy val synonyms    = Thread.currentThread.getContextClassLoader.getResourceAsStream("/linnaeus/synonyms.tsv")

  lazy val linnaeus: Matcher = new MatchPostProcessor(
    VariantDictionaryMatcher.load(dictionary, true),
    Matcher.Disambiguation.ON_WHOLE,
    true,
    null,
    new Postprocessor(Array(stopList), Array(synonyms), Array(frequencies), null, null)
  )

}
