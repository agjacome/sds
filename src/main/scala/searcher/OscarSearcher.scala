package es.uvigo.esei.tfg.smartdrugsearch.searcher

import scala.concurrent.{ ExecutionContext, Future }

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.service.OscarService

private[searcher] class OscarSearcher extends SearcherAdapter {

  import database._
  import database.profile.simple._

  lazy val oscar = OscarService()

  override def search(searchTerms : Sentence)(implicit ec : ExecutionContext) =
    oscar getNamedEntities searchTerms map (_ map oscar.normalize) map {
      normalizedTerms => searchNormalized(normalizedTerms.toSet)
    }

  private[this] def searchNormalized(normalizedTerms : Set[Sentence]) =
    database withSession { implicit session =>
      normalizedTerms flatMap {
        normalized => (Keywords findByNormalized normalized).firstOption
      }
    }

}

private[searcher] object OscarSearcher extends (() => OscarSearcher) {

  def apply( ) : OscarSearcher =
    new OscarSearcher()

}

