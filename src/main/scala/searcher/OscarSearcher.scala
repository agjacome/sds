package es.uvigo.ei.sing.sds
package searcher

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import service.OscarService

final class OscarSearcher extends SearcherAdapter {

  lazy val oscar = new OscarService

  override def search(query: String): Future[Set[Keyword.ID]] =
    for {
      entities   <- oscar.getNamedEntities(query)
      normalized <- Future.sequence(entities.map(oscar.normalize))
      keywordIds <- searchNormalized(normalized)
    } yield keywordIds

}
