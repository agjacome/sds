package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{ choose, alphaStr }

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class AnnotationSpec extends BaseSpec {

  private[this] lazy val annotationGenerator = annotationTupleGenerator map Annotation.tupled

  private[this] lazy val annotationTupleGenerator = for {
    id            <- arbitrary[Option[Long]] map (_ map AnnotationId)
    documentId    <- arbitrary[Long] map DocumentId
    keywordId     <- arbitrary[Long] map KeywordId
    startPosition <- choose(0, Long.MaxValue / 2) map Position
    endPosition   <- choose(startPosition.value, Long.MaxValue) map Position
    text          <- nonEmptyStringGenerator map Sentence
  } yield (id, documentId, keywordId, text, startPosition, endPosition)

  private[this] lazy val invalidPosAnnotationTupleGenerator = for {
    endPosition   <- choose(0, Long.MaxValue / 2) map Position
    startPosition <- choose(endPosition.value + 1, Long.MaxValue) map Position
    (id, documentId, keywordId, text, _, _) <- annotationTupleGenerator
  } yield (id, documentId, keywordId, text, startPosition, endPosition)

  private[this] def createJson(annotation : Annotation) =
    JsObject(Seq(
      annotation.id map ("id" -> Json.toJson(_))
    ).flatten ++ Seq(
      "documentId"    -> JsNumber(annotation.documentId.value),
      "keywordId"     -> JsNumber(annotation.keywordId.value),
      "text"          -> JsString(annotation.text.toString),
      "startPosition" -> JsNumber(annotation.startPosition.value),
      "endPosition"   -> JsNumber(annotation.endPosition.value)
    ))


  "An Annotation" - {

    "can be constructed" - {

      "with an optional Annotation ID, a Document ID, a Keyword ID, a text Sentence, and start and ending Positions" in {
        forAll(annotationTupleGenerator) { case (id, documentId, keywordId, text, start, end) =>
          Annotation(id, documentId, keywordId, text, start, end) should have (
            'id            (id),
            'documentId    (documentId.value),
            'keywordId     (keywordId.value),
            'text          (text),
            'startPosition (start.value),
            'endPosition   (end.value)
          )
        }
      }

      "by parsin a JSON object" in {
        forAll(annotationGenerator) { annotation : Annotation =>
          createJson(annotation).as[Annotation] should equal (annotation)
        }
      }

    }

    "can be transformed to a JSON object" in {
      forAll(annotationGenerator) { annotation : Annotation =>
        (Json toJson annotation) should equal (createJson(annotation))
      }
    }

    "should throw an IllegalArgumentException" - {

      "when constructed with an Starting Position bigger than the Ending Position" in {
        forAll(invalidPosAnnotationTupleGenerator) { case (id, documentId, keywordId, text, start, end) =>
          a [IllegalArgumentException] should be thrownBy {
            val annotation = Annotation(id, documentId, keywordId, text, start, end)
          }
        }
      }

    }


  }

}

