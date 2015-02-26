package es.uvigo.ei.sing.sds.service

import scala.collection.JavaConversions._
import scala.concurrent.{ ExecutionContext, Future }

import play.api.cache.Cache
import play.api.Play.current

import uk.ac.man.entitytagger.Mention

import es.uvigo.ei.sing.sds.entity.Sentence

class LinnaeusService private {

  import LinnaeusService.linnaeus

  lazy val eUtils = EUtilsService()

  def obtainMentions(text : String)(implicit ec : ExecutionContext) : Future[Seq[Mention]] =
    Future { linnaeus `match` text }

  def normalize(mention : Mention) : Sentence =
    getScientificName((mention.getMostProbableID split ":").last.toLong)

  private[this] def getScientificName(ncbiId : Long) =
    Cache.getAs[String](s"TaxonomyId($ncbiId)") getOrElse {
      val name = (eUtils taxonomyScientificName ncbiId).fold(s"NCBI Taxonomy ID: $ncbiId")(Sentence(_))
      Cache.set(s"TaxonomyId($ncbiId)", name)
      name
    }

}

object LinnaeusService extends (() => LinnaeusService) {

  import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
  import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

  private lazy val dictionary  = getClass.getResourceAsStream("/linnaeus/dict-species.tsv")
  private lazy val frequencies = getClass.getResourceAsStream("/linnaeus/freq-species.tsv")
  private lazy val stopList    = getClass.getResourceAsStream("/linnaeus/stoplist.tsv")
  private lazy val synonyms    = getClass.getResourceAsStream("/linnaeus/synonyms.tsv")

  lazy val linnaeus : Matcher = new MatchPostProcessor(
    VariantDictionaryMatcher.load(dictionary, true),
    Matcher.Disambiguation.ON_WHOLE,
    true,
    null,
    new Postprocessor(Array(stopList), Array(synonyms), Array(frequencies), null, null)
  )

  def apply( ) : LinnaeusService =
    new LinnaeusService

}

