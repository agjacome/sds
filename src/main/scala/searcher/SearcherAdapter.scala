package es.uvigo.ei.sing.sds.searcher

import scala.concurrent.{ ExecutionContext, Future }

import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity._

private[searcher] trait SearcherAdapter {

  lazy val database = DatabaseProfile()

  def search(searchTerms : Sentence)(implicit ec : ExecutionContext) : Future[Set[Keyword]]

}
