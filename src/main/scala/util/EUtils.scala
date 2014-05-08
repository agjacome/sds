package es.uvigo.esei.tfg.smartdrugsearch.util

import play.api.Logger

import scalaxb.DataRecord
import scalaxb.generated._

class EUtils private {

  // All methods present here are blocking, and should be wrapped inside a
  // Future whenever being reactive is a requirement, as they perform
  // long-running operations (SOAP calls). Example usage:
  //
  //   future { taxonomyScientificName(9606) } map {
  //     scientificName => doSomethingWithTheScientificName(scientificName)
  //   }
  //
  // That way, the method call will be non-blocking, and the "doSomething..."
  // operation will be done once the web service call completes.

  def findPubMedIDs(terms : String, relDate : Option[Int], retStart : Int, retMax : Int) : (Long, Set[Long]) = {
    val res = searchInPubMed(terms, relDate, retStart, retMax)
    (getCountFromResult(res), getIdListFromResult(res))
  }

  def searchInPubMed(terms : String, relDate : Option[Int], retStart : Int, retMax : Int) : Option[ESearchResult] =
    eUtilsService.run_eSearch(
      db       = Some("pubmed"),
      term     = Some(terms),
      datetype = Some("edat"),
      reldate  = relDate map (_.toString),
      retStart = Some(retStart.toString),
      retMax   = Some(retMax.toString),
      // here comes the bullshit: why not default all params to None and allow
      // us scalaxb users use only whichever ones we need?
      webEnv = None, queryKey = None, usehistory = None, tool = None, email = None, field = None, mindate = None,
      maxdate = None, rettype = None, sort = None
    ) match {
      case Right(result) => Some(result)
      case Left(fault)   => Logger.error(fault.toString); None
    }

  def fetchPubMedArticles(ids : Seq[Long]) : Seq[(Long, String, String)] =
    eUtilsService.run_eFetch(Some(ids mkString ","), None, None, None, None, None, None, Some("abstract")) match {
      case Right(result) => parsePubMedResult(result)
      case Left(fault)   => Logger.error(fault.toString); Seq.empty
    }

  def taxonomyScientificName(id : Long) : Option[String] =
    eUtilsService.run_eSummary(Some("taxonomy"), Some(id.toString), None, None, None, None, None, None) match {
      case Right(summary) => parseScientificName(summary)
      case Left(fault)    => Logger.error(fault.toString); None
    }

  private def getCountFromResult(result : Option[ESearchResult]) =
    result.fold(0L) { _.Count.fold(0L)(_.toLong) }

  private def getIdListFromResult(result : Option[ESearchResult]) =
    result.fold(Set.empty[Long]) { _.IdList.fold(Set.empty[Long])(_.Id.map(_.toLong).toSet) }

  private def parsePubMedResult(result : EFetchResult) =
    result.PubmedArticleSet.fold(Seq.empty[(Long, String, String)])(parsePubMedArticleSet)

  private def parsePubMedArticleSet(articles : PubmedArticleSet) =
    articles.pubmedarticlesetoption flatMap {
      case DataRecord(_, _, PubmedArticleType(medline,  _)) => parseArticleType(medline)
      case DataRecord(_, _, PubmedBookArticleType(book, _)) => parseBookArticleType(book)
    }

  private def parseArticleType(medline : MedlineCitationType) =
    medline.Article.Abstract map {
      abs => (medline.PMID.value.toLong, medline.Article.ArticleTitle.value, abs.AbstractText.head.value)
    }

  private def parseBookArticleType(book : BookDocumentType) =
    (book.ArticleTitle, book.Abstract) match {
      case (Some(tit), Some(abs)) => Some(book.PMID.value.toLong, tit.value, abs.AbstractText.head.value)
      case _                      => None
    }

  private def parseScientificName(summary : ESummaryResult) =
    summary.DocSum flatMap (_.Item) filter (_.Name == "ScientificName") match {
      case item :: _ => item.ItemContent
      case Nil       => None
    }

}

object EUtils extends (() => EUtils) {

  def apply( ) : EUtils =
    new EUtils

}
