package es.uvigo.ei.sing.sds
package searcher

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import service.ABNERService

final class SimpleSearcher extends SearcherAdapter {

  override def search(query: String): Future[Set[Keyword.ID]] =
    searchNormalized(query.toLowerCase.split("\\s+").toSet)

}
