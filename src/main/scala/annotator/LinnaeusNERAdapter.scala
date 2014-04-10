package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.collection.JavaConversions._
import scala.concurrent.{ Await, Future, future }
import scala.concurrent.duration._
import scala.xml.{ Node, XML }

import play.api.libs.ws.{ WS, Response }
import play.api.libs.concurrent.Execution.Implicits._

import uk.ac.man.entitytagger.Mention
import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

private[annotator] class LinnaeusNERAdapter extends NERAdapter {

  import context._
  import db.profile.simple._
  import LinnaeusNERAdapter._

  private[this] val Keywords    = KeywordsDAO()
  private[this] val Annotations = AnnotationsDAO()

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

  private[this] def insertAnnotation(keyword : Keyword, annotation : Annotation) : Unit = {
    val current = (Keywords findById keyword.id).get
    Annotations save annotation
    Keywords save (current copy (occurrences = current.occurrences + 1))
  }

  private[this] def normalize(ncbiId : String) : Future[Keyword] =
    summarize(ncbiId) map {
      summary => recoverKeyword(
        (summary \\ "Item" filter { _ \\ "@Name" exists (_.text == "ScientificName") }).text
      )
    }

  private[this] def summarize(ncbiId : String) : Future[Node] =
    (webService withQueryString ("id" -> ncbiId)).get map {
      res => (XML.loadString(res.body) \\ "DocSum").head
    }

  private[this] def recoverKeyword(normalized : Sentence) : Keyword =
    (Keywords findByNormalized normalized) match {
      case Some(keyword) => keyword
      case None          => Keywords save Keyword(None, normalized, Species)
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

  private lazy val webService = WS.url(
    "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi"
  ) withQueryString ("db" -> "taxonomy")

}

