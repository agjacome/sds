package es.uvigo.esei.tfg.smartdrugsearch.util

import play.api.Logger

import scalaxb.DataRecord
import scalaxb.generated._

object EUtils {

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

  def findByTermsInPubMed(
    terms    : String,
    relDate  : Option[Int] = None,
    retStart : Int         = 0,
    retMax   : Int         = 100
  ) : Option[ESearchResult] =
    eUtilsService.run_eSearch(
      db       = Some("pubmed"),
      term     = Some(terms),
      datetype = Some("edat"),
      reldate  = relDate map (_.toString),
      retStart = Some(retStart.toString),
      retMax   = Some(retMax.toString),
      webEnv   = None, queryKey = None, usehistory = None, tool = None, email = None, field = None,
      mindate  = None, maxdate  = None, rettype    = None, sort = None
    ) match {
      case Right(result) => Some(result)
      case Left(fault)   => Logger.error(fault.toString); None
    }

  def fetchPubMedArticles(ids : Seq[Long]) : Seq[(Long, String, String)] =
    eUtilsService.run_eFetch(
      Some(ids mkString ","), None, None, None, None, None, None, Some("abstract")
    ) match {
      case Right(result) => parsePubMedResult(result)
      case Left(fault)   => Logger.error(fault.toString); Seq.empty
    }

  def taxonomyScientificName(id : Long) : Option[String] =
    eUtilsService.run_eSummary(
      Some("taxonomy"), Some(id.toString), None, None, None, None, None, None
    ) match {
      case Right(summary) => parseScientificName(summary)
      case Left(fault)    => Logger.error(fault.toString); None
    }

  private def parsePubMedResult(result : EFetchResult) =
    result.PubmedArticleSet match {
      case Some(articles) => parsePubMedArticleSet(articles)
      case None           => Seq.empty
    }

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

