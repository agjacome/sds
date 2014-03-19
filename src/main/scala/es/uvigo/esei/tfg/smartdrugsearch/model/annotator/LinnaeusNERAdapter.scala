package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

import scala.collection.JavaConversions._
import scala.concurrent.{ Await, Future, future }
import scala.concurrent.duration._
import scala.xml.{ Node, XML }

import play.api.libs.ws.{ WS, Response }
import play.api.libs.concurrent.Execution.Implicits._

import uk.ac.man.entitytagger.Mention
import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

import es.uvigo.esei.tfg.smartdrugsearch.model._
import es.uvigo.esei.tfg.smartdrugsearch.model.Category.Species
import es.uvigo.esei.tfg.smartdrugsearch.model.database.{ DAL, current }

private[annotator] class LinnaeusNERAdapter (protected val dal : DAL = current.dal) extends NERAdapter {

  import context._
  import dal._
  import dal.profile.simple._
  import LinnaeusNERAdapter._

  private[this] val cache = new com.twitter.util.LruMap[String, NamedEntity](10)

  override final protected def annotate(document : Document) : Future[FinishedAnnotation] =
    obtainMentions(document.text) map { mentions =>
      mentions foreach { saveMention(_, document) }
      FinishedAnnotation(document)
    }

  private[this] def obtainMentions(text : String) : Future[Seq[Mention]] =
    future { linnaeusProcessor `match` text }

  private[this] def saveMention(mention : Mention, document : Document) : Unit = {
    val namedEntity = getEntity((mention.getMostProbableID split ":").last)
    val annotation  = getAnnotation(mention, namedEntity, document)
    insertAnnotation(namedEntity, annotation)
  }

  private[this] def getEntity(ncbiId : String) : NamedEntity =
    cache getOrElseUpdate (ncbiId, Await.result(normalize(ncbiId), 5.seconds))

  private[this] def getAnnotation(m : Mention, e : NamedEntity, d : Document) : Annotation =
    Annotation(None, d.id.get, e.id.get, m.getText, m.getStart, m.getEnd)

  private[this] def insertAnnotation(entity : NamedEntity, annotation : Annotation) : Unit = {
    val counterQuery = NamedEntities byId (entity.id.get) map (_.occurrences)
    session withTransaction {
      Annotations += annotation
      counterQuery update (counterQuery.first + 1)
    }
  }

  private[this] def normalize(ncbiId : String) : Future[NamedEntity] =
    summarize(ncbiId) map {
      summary => recoverEntity(
        (summary \\ "Item" filter { _ \\ "@Name" exists (_.text == "ScientificName") }).text
      )
    }

  private[this] def summarize(ncbiId : String) : Future[Node] =
    (webService withQueryString ("id" -> ncbiId)).get map {
      res => (XML.loadString(res.body) \\ "DocSum").head
    }

  private[this] def recoverEntity(normalized : Sentence) : NamedEntity =
    session withTransaction {
      (NamedEntities byNormalized normalized).firstOption match {
        case Some(entity) => entity
        case None         => insertEntity(NamedEntity(None, normalized, Species))
      }
    }

  private[this] def insertEntity(entity : NamedEntity) : NamedEntity =
    entity copy (id = NamedEntities returning (NamedEntities map (_.id.?)) insert entity)

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

  private lazy val linnaeusProcessor = new MatchPostProcessor(
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

