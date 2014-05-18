package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class KeywordSpec extends BaseSpec {

  private[this] lazy val keywordGenerator = keywordTupleGenerator map Keyword.tupled

  private[this] lazy val keywordTupleGenerator = for {
    id          <- arbitrary[Option[Long]] map (_ map KeywordId)
    normalized  <- nonEmptyStringGenerator map Sentence
    category    <- Gen.oneOf(Compound, Drug, Gene, Protein, Species)
    occurrences <- Gen.choose(0, Long.MaxValue) map Size
  } yield (id, normalized, category, occurrences)

  private[this] def createJson(keyword : Keyword) =
    JsObject(Seq(
      keyword.id map ("id" -> Json.toJson(_))
    ).flatten ++ Seq(
      "normalized"  -> JsString(keyword.normalized.toString),
      "category"    -> JsString(keyword.category.toString),
      "occurrences" -> JsNumber(keyword.occurrences.value)
    ))


  "A Keyword" - {

    "can be constructed" - {

      "with an optional Keyword ID, a normalized text Sentence, a Category and the number of occurrences" in {
        forAll(keywordTupleGenerator) { case (id, normalized, category, occurrences) =>
          Keyword(id, normalized, category, occurrences) should have (
            'id          (id),
            'normalized  (normalized),
            'category    (category),
            'occurrences (occurrences.value)
          )
        }
      }

      "by parsing a JSON object" in {
        forAll(keywordGenerator) { keyword : Keyword =>
          createJson(keyword).as[Keyword] should equal (keyword)
        }
      }

    }

    "can be transformed to a JSON object" in {
      forAll(keywordGenerator) { keyword : Keyword =>
        (Json toJson keyword) should equal (createJson(keyword))
      }
    }

  }

}

