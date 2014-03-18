package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

import scala.collection.JavaConversions._
import scala.concurrent.{ Await, Future, future }
import scala.concurrent.duration._
import scala.util.{ Success, Failure }
import scala.xml.{ Node, XML }

import akka.actor.Props

import play.api.libs.ws.{ WS, Response }
import play.api.libs.concurrent.Execution.Implicits._

import uk.ac.man.entitytagger.Mention
import uk.ac.man.entitytagger.matching.{ Matcher, Postprocessor }
import uk.ac.man.entitytagger.matching.matchers.{ MatchPostProcessor, VariantDictionaryMatcher }

import es.uvigo.esei.tfg.smartdrugsearch.model._
import es.uvigo.esei.tfg.smartdrugsearch.model.Category.Species
import es.uvigo.esei.tfg.smartdrugsearch.model.database.{ DAL, current }

private[annotator] class LinnaeusNERAdapter (protected val dal : DAL = current.dal) extends NERAdapter {

  import dal._
  import dal.profile.simple._
  import context.dispatcher
  import LinnaeusNERAdapter._

  private[this] val cache = new com.twitter.util.LruMap[String, NamedEntity](10)

  override final protected def annotate(document : Document) : Unit =
    obtainMentions(document.text) onComplete {
      case Success(mentions) => mentions foreach { saveInDatabase(_, document) }
      case Failure(error)    => play.api.Logger.error(error.getMessage); throw error
    }

  private[this] def obtainMentions(text : String) : Future[Seq[Mention]] =
    future { matchpp `match` text }

  private[this] def saveInDatabase(mention : Mention, document : Document) : Unit = {
    val (entity, annotation) = transform(mention, document)
    val counterQuery = NamedEntities byId (entity.id.get) map (_.occurrences)

    session withTransaction {
      Annotations += annotation
      counterQuery update (counterQuery.first + 1)
    }
  }

  private[this] def transform(mention : Mention, document : Document) : (NamedEntity, Annotation) = {
    val ncbiId = (mention.getMostProbableID split ":").last
    val entity = getEntity(ncbiId)
    (entity, Annotation(None, document.id.get, entity.id.get, mention.getText, mention.getStart, mention.getEnd))
  }

  private[this] def getEntity(ncbiId : String) : NamedEntity =
    cache getOrElseUpdate (ncbiId, Await.result(normalize(ncbiId), 600.seconds))

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
    NamedEntities.byNormalized(normalized).firstOption match {
      case Some(entity) => entity
      case None         => insertEntity(NamedEntity(None, normalized, Species))
    }

  private[this] def insertEntity(entity : NamedEntity) : NamedEntity = {
    val id = NamedEntities returning NamedEntities.map(_.id) insert entity
    entity copy (id = Some(id))
  }

}

object LinnaeusNERAdapter {

  // All resources and necessary objects for the Linnaeus annotator are kept
  // inside the companion object since they are too expensive to load, and this
  // way we make sure that they are only created once, independent of how many
  // instances of LinnaeusNERAdapter exist; they work mostly like static fields.
  // Also, all of them are lazy to prevent wasteful eager inizialization given
  // the case that a LinnaeusNERAdapter instance is created, but never used.

  private lazy val dict = getClass.getResourceAsStream("/linnaeus/dict-species.tsv")
  private lazy val freq = getClass.getResourceAsStream("/linnaeus/freq-species.tsv")
  private lazy val stop = getClass.getResourceAsStream("/linnaeus/stoplist.tsv")
  private lazy val syno = getClass.getResourceAsStream("/linnaeus/synonyms.tsv")

  private lazy val matcher = VariantDictionaryMatcher.load(dict, true)
  private lazy val process = new Postprocessor(Array(stop), Array(syno), Array(freq), null, null)
  private lazy val matchpp = new MatchPostProcessor(matcher, Matcher.Disambiguation.ON_WHOLE, true, null, process)

  private lazy val webService = WS.url(
    "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi"
  ) withQueryString ("db" -> "taxonomy")

}

