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

  def download(pmidsToDownload: Set[Article.PMID]): Future[Set[Article]] = {
    val existingArticles = Future.sequence {
      pmidsToDownload.map(articlesDAO.getByPubmedId)
    } map { _.flatten }

    for {
      existing <- existingArticles
      pmids     = pmidsToDownload -- existing.flatMap(_.pubmedId)
      fetched  <- eUtils.fetchPubMedArticles(pmids)
      articles <- articlesDAO.insert(fetched.map(_._1).toSeq: _*)
      // TODO: insert authors
    } yield existing ++ articles
  }

}
