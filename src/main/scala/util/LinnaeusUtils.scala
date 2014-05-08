package es.uvigo.esei.tfg.smartdrugsearch.util

import com.twitter.util.LruMap

import uk.ac.man.entitytagger.Mention

class LinnaeusUtils private {

  import scala.collection.JavaConversions._
  import LinnaeusUtils._

  private lazy  val eUtils = EUtils()
  private[this] val cache = new LruMap[Long, String](128)

  def obtainMentions(text : String) : Seq[Mention] =
    linnaeus `match` text

  def normalize(mention : Mention) : String =
    getScientificName(mention.getMostProbableID.split(":").last.toLong)

  private[this] def getScientificName(ncbiId : Long) =
    cache getOrElseUpdate (ncbiId, (eUtils taxonomyScientificName ncbiId) getOrElse s"NCBI Taxonomy ID: $ncbiId")

}

object LinnaeusUtils extends (() => LinnaeusUtils) {

  import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
  import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

  private lazy val dictionary  = getClass getResourceAsStream "/linnaeus/dict-species.tsv"
  private lazy val frequencies = getClass getResourceAsStream "/linnaeus/freq-species.tsv"
  private lazy val stopList    = getClass getResourceAsStream "/linnaeus/stoplist.tsv"
  private lazy val synonyms    = getClass getResourceAsStream "/linnaeus/synonyms.tsv"

  lazy val linnaeus : Matcher = new MatchPostProcessor(
    VariantDictionaryMatcher.load(dictionary, true),
    Matcher.Disambiguation.ON_WHOLE,
    true,
    null,
    new Postprocessor(Array(stopList), Array(synonyms), Array(frequencies), null, null)
  )

  def apply( ) : LinnaeusUtils =
    new LinnaeusUtils

}

