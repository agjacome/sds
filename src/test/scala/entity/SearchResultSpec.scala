package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import org.scalacheck.Gen

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity.Generators._

class SearchResultSpec extends BaseSpec {

  private[this] lazy val searchResultGenerator  = searchResultTupleGenerator  map SearchResult.tupled
  private[this] lazy val searchResultsGenerator = searchResultsTupleGenerator map SearchResults.tupled

  private[this] lazy val searchResultTupleGenerator = for {
    document   <- documentGenerator
    keywordSet <- Gen.containerOf[Set, Keyword](keywordGenerator)
  } yield (document, keywordSet)

  private[this] lazy val searchResultsTupleGenerator = for {
    totalCount <- Gen.choose(0L, Long.MaxValue) map Size
    pageNumber <- Gen.choose(0L, Long.MaxValue) map Position
    pageSize   <- Gen.choose(0L, Long.MaxValue) map Size
    // ScalaCheck cannot generate enough valid values for this, so a seq of only
    // one element is used as a simple fix:
    // results <- Gen.containerOf[Seq, SearchResult](searchResultGenerator)
    results    <- searchResultGenerator map { s => Seq(s) }
  } yield (totalCount, pageNumber, pageSize, results)

  private[this] def createJson(searchResult : SearchResult) =
    Json.obj(
      "document" -> Json.toJson(searchResult.document),
      "keywords" -> JsArray((searchResult.keywords map { k => Json toJson k }).toSeq)
    )

  private[this] def createJson(searchResults : SearchResults) =
    Json.obj(
      "totalCount" -> JsNumber(searchResults.totalCount.value),
      "pageNumber" -> JsNumber(searchResults.pageNumber.value),
      "pageSize"   -> JsNumber(searchResults.pageSize.value),
      "results"    -> JsArray((searchResults.results map { s => Json toJson s }))
    )

  "The SearchResult entity" - {

    "can be constructed with a Document and a Set of Keywords related to that Document" in {
      forAll(searchResultTupleGenerator) { case (document, keywordSet) =>
        SearchResult(document, keywordSet) should have (
          'document (document),
          'keywords (keywordSet)
        )
      }
    }

    "can be transformed to a JSON object" in {
      forAll(searchResultGenerator) { searchResult : SearchResult =>
        (Json toJson searchResult) should equal (createJson(searchResult))
      }
    }

  }

  "The SearchResults entity" - {

    "can be constructed with a totalCount Size, a pageNumber Position, a pageSize and a Seq of SearchResults" in {
      forAll(searchResultsTupleGenerator) { case (totalCount, pageNumber, pageSize, results) =>
        SearchResults(totalCount, pageNumber, pageSize, results) should have (
          'totalCount (totalCount.value),
          'pageNumber (pageNumber.value),
          'pageSize   (pageSize.value),
          'results    (results)
        )
      }
    }

    "can be transformed to a JSON object" in {
      forAll(searchResultsGenerator) { searchResults : SearchResults =>
        (Json toJson searchResults) should equal (createJson(searchResults))
      }
    }

  }

}
