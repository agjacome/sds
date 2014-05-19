package es.uvigo.esei.tfg.smartdrugsearch.searcher

import scala.concurrent.{ ExecutionContext, Future }

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[searcher] trait SearcherAdapter {

  lazy val database = DatabaseProfile()

  def search(searchTerms : Sentence)(implicit ec : ExecutionContext) : Future[Set[Keyword]]

}
