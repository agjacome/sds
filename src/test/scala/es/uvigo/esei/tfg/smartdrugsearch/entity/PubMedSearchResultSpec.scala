package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class PubMedSearchResultSpec extends BaseSpec {

  private[this] lazy val pubmedSearchResultGenerator = pubmedSearchResultTupleGenerator map PubMedSearchResult.tupled

  private[this] lazy val pubmedSearchResultTupleGenerator = for {
    totalResults <- Gen.choose(0L, Long.MaxValue) map Size
    firstElement <- Gen.choose(0L, Long.MaxValue) map Position
    idList       <- Gen.containerOf[Set, PubMedId](Gen.choose(Long.MinValue, Long.MaxValue) map PubMedId)
  } yield (totalResults, firstElement, idList)

  private[this] def createJson(pubmedSearchResult : PubMedSearchResult) =
    Json.obj(
      "totalResults" -> JsNumber(pubmedSearchResult.totalResults.value),
      "firstElement" -> JsNumber(pubmedSearchResult.firstElement.value),
      "idList"       -> JsArray((pubmedSearchResult.idList map { id => JsNumber(id.value) }).toSeq)
    )

  "A PubMedSearchResult" - {

    "can be constructed" - {
      "with the total Size of results, the first Position of this result and a Set of PubMed IDs" in {
        forAll(pubmedSearchResultTupleGenerator) { case (totalResults, firstElement, idList) =>
          PubMedSearchResult(totalResults, firstElement, idList) should have (
            'totalResults (totalResults.value),
            'firstElement (firstElement.value),
            'idList       (idList)
          )
        }
      }
    }

    "can be transformed to a JSON object" in {
      forAll(pubmedSearchResultGenerator) { pubmedSearchResult : PubMedSearchResult =>
        (Json toJson pubmedSearchResult) should equal (createJson(pubmedSearchResult))
      }
    }

  }

}

