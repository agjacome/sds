package es.uvigo.ei.sing.sds
package provider

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import entity._
import database._
import service.EUtilsService
import util.Page

final class PubMedProvider {

  lazy val eUtils       = new EUtilsService
  lazy val articlesDAO  = new ArticlesDAO
  lazy val authorsDAO   = new AuthorsDAO
  lazy val authoringDAO = new ArticleAuthorsDAO

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
      articles <- insertArticles(fetched)
    } yield existing ++ articles
  }

  private def insertArticles(fetched: Set[(Article, List[Author])]): Future[Set[Article]] =
    Future.sequence(fetched.map { case (article, authors) =>
      for {
        art <- articlesDAO.insert(article)
        aut <- insertOrRetrieveAuthors(authors.toSet)
        _   <- associateArticleAuthors(art, aut, authors)
      } yield art
    })

  private def insertOrRetrieveAuthors(as: Set[Author]): Future[Set[Author]] =
    Future.sequence(as.map(authorsDAO.getByNameOrInsert))

  private def associateArticleAuthors(art: Article, auts: Set[Author], order: List[Author]): Future[Unit] = {
    import authoringDAO.ArticleAuthor

    // FIXME: unsafe .get
    val aas: List[ArticleAuthor] = order.zipWithIndex flatMap { case (a1, idx) =>
      auts.find(a2 =>
        a1.firstName.equalsIgnoreCase(a2.firstName) &&
        a1.lastName.equalsIgnoreCase(a2.lastName)   &&
        a1.initials.equalsIgnoreCase(a2.initials)
      ).map(a => (art.id.get, a.id.get, idx + 1))
    }

    authoringDAO.insert(aas: _*)
  }

}
