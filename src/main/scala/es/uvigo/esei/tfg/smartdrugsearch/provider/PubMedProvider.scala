package es.uvigo.esei.tfg.smartdrugsearch.provider

import scala.concurrent.{ ExecutionContext, Future }

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.service.EUtilsService

class PubMedProvider private {

  lazy val database = DatabaseProfile()
  lazy val eUtils   = EUtilsService()

  import database.Documents
  import database.profile.simple._

  def search(terms : Sentence, limit : Option[Size], pageNumber : Position, pageSize : Size)(
    implicit ec : ExecutionContext
  ) : Future[PubMedIdList] =
    Future(eUtils findPubMedIds (terms, limit, (pageNumber - 1) * pageSize, pageSize)) map {
      case (total, ids) => PubMedIdList(total, pageNumber, pageSize, ids.toSeq)
    }

  def download(ids : Set[PubMedId])(implicit ec : ExecutionContext) : Future[Set[DocumentId]] =
    Future { eUtils fetchPubMedArticles ids } map {
      documents => (documents.par map saveDocument).seq
    }

  private[this] def saveDocument(document : Document) : DocumentId =
    database withTransaction { implicit session =>
      (Documents findByPubMedId document.pubmedId.get).firstOption match {
        case Some(doc) => doc.id.get
        case None      => Documents returning Documents.map(_.id) += document
      }
    }

}

object PubMedProvider extends (() => PubMedProvider) {

  def apply( ) : PubMedProvider =
    new PubMedProvider

}

