package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import org.scalacheck.Gen

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity.Generators._

class DocumentSpec extends BaseSpec {

  private[this] lazy val emptyTextDocumentTupleGenerator = for {
    text                                <- Gen.const("")
    (id, title, _, annotated, pubmedId) <- documentTupleGenerator
  } yield (id, title, text, annotated, pubmedId)

  private[this] def createJson(document : Document) =
    JsObject(Seq(
      document.id       map ("id"       -> Json.toJson(_)),
      document.pubmedId map ("pubmedId" -> Json.toJson(_))
    ).flatten ++ Seq(
      "title"     -> JsString(document.title.toString),
      "text"      -> JsString(document.text),
      "annotated" -> JsBoolean(document.annotated)
    ))

  "A Document" - {

    "can be constructed" - {

      "with an optional Document ID, a title Sentence, a text, an annotated Boolean flag and an optional PubMed ID" in {
        forAll(documentTupleGenerator) { case (id, title, text, annotated, pubmedId) =>
          Document(id, title, text, annotated, pubmedId) should have (
            'id        (id),
            'title     (title),
            'text      (text),
            'annotated (annotated),
            'pubmedId  (pubmedId)
          )
        }
      }

      "by parsing a JSON object" in {
        forAll(documentGenerator) { document : Document =>
          createJson(document).as[Document] should equal (document)
        }
      }

    }

    "can be transformed to a JSON object" in {
      forAll(documentGenerator) { document : Document =>
        (Json toJson document) should equal (createJson(document))
      }
    }

    "should throw an IllegalArgumentException" - {

      "whenever constructed with an empty text" in {
        forAll(emptyTextDocumentTupleGenerator) { case (id, title, text, annotated, pubmedId) =>
          a [IllegalArgumentException] should be thrownBy {
            val document = Document(id, title, text, annotated, pubmedId)
          }
        }
      }

    }

  }

}

