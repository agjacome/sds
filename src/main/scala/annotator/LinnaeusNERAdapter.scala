package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.JavaConversions._
import scala.concurrent.{ Await, Future, future }
import scala.concurrent.duration._

import uk.ac.man.entitytagger.Mention
import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.EUtils._

private[annotator] class LinnaeusNERAdapter extends NERAdapter {

  import context._
  import dbProfile.database
  import LinnaeusNERAdapter._

  private[this] val cache = new com.twitter.util.LruMap[String, Keyword](10)

  override protected def annotate(document : Document) : Future[Finished] =
    obtainMentions(document.text) map { mentions =>
      mentions foreach { saveMention(_, document) }
      Finished(document)
    }

  private[this] def obtainMentions(text : String) : Future[Seq[Mention]] =
    future { linnaeusProcessor `match` text }

  private[this] def saveMention(mention : Mention, document : Document) : Unit = {
    val keyword    = getKeyword(mention.getMostProbableID.split(":").last)
    val annotation = getAnnotation(mention, keyword, document)
    insertAnnotation(keyword, annotation)
  }

  private[this] def getKeyword(ncbiId : String) : Keyword =
    cache getOrElseUpdate (ncbiId, Await.result(normalize(ncbiId), 10.seconds))

  private[this] def getAnnotation(m : Mention, k : Keyword, d : Document) : Annotation =
    Annotation(None, d.id.get, k.id.get, m.getText, m.getStart, m.getEnd)

  private[this] def insertAnnotation(keyword : Keyword, annotation : Annotation) : Unit =
    database withTransaction { implicit session =>
      val current = (Keywords findById keyword.id).get
      Annotations save annotation
      Keywords save (current copy (occurrences = current.occurrences + 1))
    }

  private[this] def normalize(ncbiId : String) : Future[Keyword] =
    future { taxonomyScientificName(ncbiId) } map {
      name => recoverKeyword(Sentence(name getOrElse s"NCBI Taxonomy ID: ${ncbiId}"))
    }

  private[this] def recoverKeyword(normalized : Sentence) : Keyword =
    database withTransaction { implicit session =>
      (Keywords findByNormalized normalized) getOrElse (
        Keywords save Keyword(None, normalized, Species)
      )
    }

}

private object LinnaeusNERAdapter {

  // All resources and necessary objects for the Linnaeus annotator are kept
  // inside the companion object since they are too expensive to load, and this
  // way we make sure that they are only created once, independent of how many
  // instances of LinnaeusNERAdapter exist; they work mostly like static fields.
  // Also, all of them are lazy to prevent wasteful eager inizialization given
  // the case that a LinnaeusNERAdapter instance is created, but never used.

  private lazy val dictionary  = getClass.getResourceAsStream("/linnaeus/dict-species.tsv")
  private lazy val frequencies = getClass.getResourceAsStream("/linnaeus/freq-species.tsv")
  private lazy val stopList    = getClass.getResourceAsStream("/linnaeus/stoplist.tsv")
  private lazy val synonyms    = getClass.getResourceAsStream("/linnaeus/synonyms.tsv")

  private lazy val linnaeusProcessor : Matcher = new MatchPostProcessor(
    VariantDictionaryMatcher.load(dictionary, true),
    Matcher.Disambiguation.ON_WHOLE,
    true,
    null,
    new Postprocessor(Array(stopList), Array(synonyms), Array(frequencies), null, null)
  )

}

