package es.uvigo.ei.sing.sds
package searcher

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import service.DiseasesService

final class DiseasesSearcher extends SearcherAdapter {

  lazy val diseases = new DiseasesService

  override def search(query: String): Future[Set[Keyword.ID]] =
    for {
      mentions   <- diseases.getEntities(query)
      normalized <- Future.sequence(mentions.map(diseases.normalize))
      keywordIds <- searchNormalized(normalized)
    } yield keywordIds

}
