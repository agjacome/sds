package es.uvigo.ei.sing.sds
package provider

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import database._
import service.EUtilsService
import util.Page

final class PubMedProvider {

  lazy val eUtils      = new EUtilsService
  lazy val articlesDAO = new ArticlesDAO

  def search(query: String, limit: Option[Int], page: Int, pageSize: Int): Future[Page[Article.PMID]] =
    eUtils.searchArticlePMID(query, limit, page, pageSize)

  def download(ids: Set[Article.PMID]): Future[Set[Article]] =
    eUtils.fetchPubMedArticles(ids) flatMap {
      articles => articlesDAO.insert(articles.toSeq: _*)
    } map (_.toSet)

}
