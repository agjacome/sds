package es.uvigo.ei.sing.sds.service

import play.api.Logger

import scalaxb._
import scalaxb.generated._

import org.jsoup.Jsoup
import com.google.common.annotations.VisibleForTesting

import es.uvigo.ei.sing.sds.entity._

class EUtilsService private {

  // All methods present here are blocking, and should be wrapped inside a
  // Future whenever being reactive is a requirement, as they perform
  // long-running operations (SOAP calls). Example usage:
  //
  //   val eUtils = EUtilsService()
  //   Future(eUtils taxonomyScientificName 9606) map {
  //     scientificName => doSomething(scientificName)
  //   }
  //
  // That way, the method call will be non-blocking, and the "doSomething..."
  // operation will be done once the web service call completes.

  import EUtilsService.service

  def findPubMedIds(
    terms : Sentence, days : Option[Size], start : Position, count : Size
  ) : (Size, Set[PubMedId]) = {
    val res = searchInPubMed(terms, days map (_.toInt), start.toInt, count.toInt)
    (getCountFromResult(res), getIdListFromResult(res))
  }

  def fetchPubMedArticles(ids : Set[PubMedId]) : Set[Document] =
    service.run_eFetch(Some(ids mkString ","), None, None, None, None, None, None, Some("abstract")) match {
      case Right(result) => parsePubMedResult(result)
      case Left(fault)   => Logger.error(fault.toString); Set.empty
    }

  def taxonomyScientificName(id : Long) : Option[Sentence] =
    service.run_eSummary(Some("taxonomy"), Some(id.toString), None, None, None, None, None, None) match {
      case Right(summary) => parseScientificName(summary)
      case Left(fault)    => Logger.error(fault.toString); None
    }

  @VisibleForTesting
  private[service] def searchInPubMed(terms : String, relDate : Option[Int], retStart : Int, retMax : Int) : Option[ESearchResult] =
    service.run_eSearch(
      db       = Some("pubmed"),
      term     = Some(terms),
      datetype = Some("edat"),
      reldate  = relDate map (_.toString),
      retStart = Some(retStart.toString),
      retMax   = Some(retMax.toString),
      webEnv  = None, queryKey = None, usehistory = None, tool = None, email = None, field = None, mindate = None,
      maxdate = None, rettype  = None, sort       = None
    ) match {
      case Right(result) => Some(result)
      case Left(fault)   => Logger.error(fault.toString); None
    }

  private[this] def getCountFromResult(result : Option[ESearchResult]) =
    result.fold(Size(0)) { _.Count.fold(Size(0))(_.toLong) }

  private[this] def getIdListFromResult(result : Option[ESearchResult]) =
    result.fold(Set.empty[PubMedId])(_.IdList match {
      case Some(ids) => (ids.Id map { id => PubMedId(id.toLong) }).toSet
      case None      => Set.empty[PubMedId]
    })

  private[this] def parsePubMedResult(result : EFetchResult) =
    result.PubmedArticleSet.fold(Set.empty[Document])(parsePubMedArticleSet)

  private[this] def parsePubMedArticleSet(articles : PubmedArticleSet) =
    (articles.pubmedarticlesetoption flatMap {
      case DataRecord(_, _, PubmedArticleType(medline,  _)) => parseArticleType(medline)
      case DataRecord(_, _, PubmedBookArticleType(book, _)) => parseBookArticleType(book)
    }).toSet

  private[this] def parseArticleType(medline : MedlineCitationType) =
    medline.Article.Abstract map {
      abstrakt => Document(
        title    = (Jsoup parse medline.Article.ArticleTitle.value).text,
        text     = (Jsoup parse abstrakt.AbstractText.head.value).text,
        pubmedId = Some(medline.PMID.value.toLong)
      )
    }

  private[this] def parseBookArticleType(book : BookDocumentType) =
    (book.ArticleTitle, book.Abstract) match {
      case (Some(title), Some(abstrakt)) => Some(Document(
        title    = title.value,
        text     = abstrakt.AbstractText.head.value,
        pubmedId = Some(book.PMID.value.toLong)
      ))
      case _ => None
    }

  private[this] def parseScientificName(summary : ESummaryResult) =
    (summary.DocSum flatMap (_.Item) filter (_.Name == "ScientificName")).headOption flatMap {
      item => item.ItemContent map Sentence
    }

}

object EUtilsService extends (() => EUtilsService) {

  lazy val service = new EUtilsServiceSoapBindings with Soap11Clients with DispatchHttpClients { }.service

  def apply( ) : EUtilsService =
    new EUtilsService

}
