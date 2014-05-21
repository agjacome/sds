package es.uvigo.esei.tfg.smartdrugsearch.service

import akka.actor._

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

private[service] sealed trait DocumentStatsServiceMessage
case object ComputeStats extends DocumentStatsServiceMessage

private[service] trait DocumentStatsServiceBase extends Actor {

  override final def receive : Receive = {
    case ComputeStats => computeStats()
  }

  protected def computeStats( ) : Unit

  protected def getData : List[(DocumentId, KeywordId, Int, Size)]

  protected def replaceDocumentStats(data : List[(DocumentId, KeywordId, Int, Size)])

}

private[service] trait DocumentStatsServiceLogging extends DocumentStatsServiceBase {

  import play.api.Logger

  abstract override def computeStats( ) = {
    Logger.info(s"[${self.path.name}] Starting Document statistics computation")
    super.computeStats()
    Logger.info(s"[${self.path.name}] Finished Document statistics computation")
  }

  abstract override def getData = {
    Logger.debug(s"[${self.path.name}] Obtaining Documents and Keywords data")
    super.getData
  }

  abstract override def replaceDocumentStats(data : List[(DocumentId, KeywordId, Int, Size)]) = {
    Logger.debug(s"[${self.path.name}] Replacing old DocumentStatistics table data with new stats")
    super.replaceDocumentStats(data)
  }

}

private[service] class DocumentStatsServiceImpl extends DocumentStatsServiceBase {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  override protected def computeStats( ) =
    replaceDocumentStats(getData)

  override protected def getData =
    database withTransaction { implicit session =>
      (groupedData map { case ((documentId, keywordId), tuples) => 
        (documentId, keywordId, tuples.length, tuples.map(_._3).max.get)
      }).list
    }

  override protected def replaceDocumentStats(data : List[(DocumentId, KeywordId, Int, Size)]) =
    database withTransaction { implicit session =>
      DocumentStats.delete
      DocumentStats ++= data map { case (docId, keyId, docCounter, keyCounter) =>
        (docId, keyId, Size(docCounter), docCounter.toDouble / keyCounter)
      }
    }

  private[this] def groupedData(implicit session : Session) =
    Annotations join Keywords on (_.keywordId is _.id) map {
      case (a, k) => (a.documentId, a.keywordId, k.occurrences)
    } groupBy { case (documentId, keywordId, _) => (documentId, keywordId) }

}

class DocumentStatsService extends DocumentStatsServiceImpl with DocumentStatsServiceLogging

