package es.uvigo.ei.sing.sds
package searcher

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import service.LinnaeusService

final class LinnaeusSearcher extends SearcherAdapter {

  lazy val linnaeus = new LinnaeusService

  override def search(query: String): Future[Set[Keyword.ID]] =
    for {
      mentions   <- linnaeus.getMentions(query)
      normalized <- Future.sequence(mentions.map(linnaeus.normalize))
      keywordIds <- searchNormalized(normalized)
    } yield keywordIds

}
