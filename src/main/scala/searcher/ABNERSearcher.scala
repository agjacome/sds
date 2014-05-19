package es.uvigo.esei.tfg.smartdrugsearch.searcher

import scala.concurrent.{ ExecutionContext, Future }

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.service.ABNERService

private[searcher] class ABNERSearcher extends SearcherAdapter {

  import database._
  import database.profile.simple._

  lazy val abner = ABNERService()

  override def search(searchTerms : Sentence)(implicit ec : ExecutionContext) =
    abner getEntities searchTerms map (_ map abner.normalize) map {
      normalizedTerms => searchNormalized(normalizedTerms.toSet)
    }

  private[this] def searchNormalized(normalizedTerms : Set[Sentence]) =
    database withSession { implicit session =>
      normalizedTerms flatMap {
        normalized => (Keywords findByNormalized normalized).firstOption
      }
    }

}

private[searcher] object ABNERSearcher extends (() => ABNERSearcher) {

  def apply( ) : ABNERSearcher =
    new ABNERSearcher()

}

