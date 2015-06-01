package es.uvigo.ei.sing.sds
package service

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import org.jsoup.Jsoup

import scalaxb._

import entity._
import generated._
import util.Page

// TODO: parse List[Author] for each Article

final class EUtilsService extends EUtilsServiceSoapBindings with Soap11Clients with DispatchHttpClients {

  def searchArticlePMID(query: String, days: Option[Int] = None, page: Int = 0, pageSize: Int = 30): Future[Page[Article.PMID]] =
    Future {
      val offset = page * pageSize
      val result = searchPubMed(query, days, offset, pageSize)
      Page(extractResultPMIDs(result).toSeq, page, offset, countResults(result))
    }

  def fetchPubMedArticles(pmids: Set[Article.PMID]): Future[Seq[Article]] =
    Future {
      service.run_eFetch(
        id        = Some(pmids mkString ","),
        webEnv    = None,
        query_key = None,
        tool      = None,
        email     = None,
        retstart  = None,
        retmax    = None,
        rettype   = Some("abstract")
      ).right.toOption.fold(Set.empty[Article])(parseResultArticles).toSeq
    }

  def fetchTaxonomyScientificName(taxonomyId: Long): Future[Option[String]] =
    Future {
      service.run_eSummary(
        db        = Some("taxonomy"),
        id        = Some(taxonomyId.toString),
        webEnv    = None,
        query_key = None,
        retstart  = None,
        retmax    = None,
        tool      = None,
        email     = None
      ).right.toOption.flatMap(parseScientificName)
    }

  private def searchPubMed(query: String, reldate: Option[Int], retstart: Int, retmax: Int): Option[ESearchResult] =
    service.run_eSearch(
      db         = Some("pubmed"),
      term       = Some(query),
      datetype   = Some("edat"),
      reldate    = reldate.map(_.toString),
      retStart   = Some(retstart.toString),
      retMax     = Some(retmax.toString),
      webEnv     = None,
      queryKey   = None,
      usehistory = None,
      tool       = None,
      email      = None,
      field      = None,
      mindate    = None,
      maxdate    = None,
      rettype    = None,
      sort       = None
    ).right.toOption

  private def countResults(result: Option[ESearchResult]): Int =
    result.flatMap(_.Count).fold(0)(_.toInt)

  private def extractResultPMIDs(result: Option[ESearchResult]): Set[Article.PMID] =
    result.flatMap(_.IdList).fold(Set.empty[Article.PMID])(_.Id.map(_.toLong).toSet)

  private def parseResultArticles(result: EFetchResult): Set[Article] =
    result.PubmedArticleSet.fold(Set.empty[Article])(parseArticleSet)

  private def parseArticleSet(articles: PubmedArticleSet): Set[Article] =
    articles.pubmedarticlesetoption flatMap {
      case DataRecord(_, _, PubmedArticleType(medline, _))  => parseArticleType(medline)
      case DataRecord(_, _, PubmedBookArticleType(book, _)) => parseBookType(book)
    } toSet

  private def parseArticleType(medline: MedlineCitationType): Option[Article] =
    medline.Article.Abstract map { 
      txt => Article(
        Some(medline.PMID.value.toLong),
        Jsoup.parse(medline.Article.ArticleTitle.value).text,
        Jsoup.parse(txt.AbstractText.head.value).text
      )
    }

  private def parseBookType(book: BookDocumentType): Option[Article] =
    book.ArticleTitle.flatMap(title => book.Abstract.map(txt => (title, txt))) map {
      case (title, txt) => Article(
        Some(book.PMID.value.toLong),
        title.value,
        txt.AbstractText.head.value
      )
    }

  private def parseScientificName(summary: ESummaryResult): Option[String] = {
    val items = summary.DocSum.flatMap(_.Item).filter(_.Name == "ScientificName")
    items.headOption.flatMap(_.ItemContent)
  }

}
