package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.JavaConversions._
import scala.concurrent.{ Await, future }
import scala.concurrent.duration._

import uk.ac.man.entitytagger.Mention

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.EUtils._

private[annotator] class LinnaeusNERAdapter extends NERAdapter {

  import context._
  import LinnaeusNERAdapter._

  private[this] val cache = new com.twitter.util.LruMap[Long, Keyword](10)

  override protected def annotate(document : Document) =
    obtainMentions(document.text) map { mentions =>
      mentions foreach { saveMention(_, document) }
      Finished(document)
    }

  private[this] def obtainMentions(text : String) =
    future { linnaeusProcessor `match` text }

  private[this] def saveMention(mention : Mention, document : Document) = {
    val keyword    = getKeyword(mention.getMostProbableID.split(":").last.toLong)
    val annotation = getAnnotation(mention, keyword, document)
    storeAnnotation(keyword, annotation)
  }

  private[this] def getKeyword(ncbiId : Long) =
    cache getOrElseUpdate (ncbiId, Await.result(normalize(ncbiId), 10.seconds))

  private[this] def getAnnotation(m : Mention, k : Keyword, d : Document) =
    Annotation(None, d.id.get, k.id.get, m.getText, m.getStart, m.getEnd)

  private[this] def normalize(ncbiId : Long) =
    future { taxonomyScientificName(ncbiId) } map {
      name => getOrStoreNewKeyword(Sentence(name getOrElse s"NCBI Taxonomy ID: ${ncbiId}"), Species)
    }

}

private object LinnaeusNERAdapter {

  // All resources and necessary objects for the Linnaeus annotator are kept
  // inside the companion object since they are too expensive to load, and this
  // way we make sure that they are only created once, independent of how many
  // instances of LinnaeusNERAdapter exist; they work mostly like static fields.
  // Also, all of them are lazy to prevent wasteful eager initialization given
  // the case that a LinnaeusNERAdapter instance is created, but never used.

  import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
  import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

  private lazy val dictionary  = getClass getResourceAsStream "/linnaeus/dict-species.tsv"
  private lazy val frequencies = getClass getResourceAsStream "/linnaeus/freq-species.tsv"
  private lazy val stopList    = getClass getResourceAsStream "/linnaeus/stoplist.tsv"
  private lazy val synonyms    = getClass getResourceAsStream "/linnaeus/synonyms.tsv"

  private lazy val linnaeusProcessor : Matcher = new MatchPostProcessor(
    VariantDictionaryMatcher.load(dictionary, true),
    Matcher.Disambiguation.ON_WHOLE,
    true,
    null,
    new Postprocessor(Array(stopList), Array(synonyms), Array(frequencies), null, null)
  )

}

