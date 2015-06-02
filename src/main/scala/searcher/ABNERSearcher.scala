package es.uvigo.ei.sing.sds
package searcher

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import service.ABNERService

final class ABNERSearcher extends SearcherAdapter {

  lazy val abner = new ABNERService

  override def search(query: String): Future[Set[Keyword.ID]] =
    for {
      entities   <- abner.getEntities(query)
      normalized <- Future.sequence(entities.map(abner.normalize))
      keywordIds <- searchNormalized(normalized)
    } yield keywordIds

}
