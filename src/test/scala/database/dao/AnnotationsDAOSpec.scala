package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.{ DatabaseBaseSpec, DatabaseProfile }

class AnnotationsDAOSpec extends DatabaseBaseSpec {

  import dbProfile.{ Annotations, Documents, Keywords }
  import dbProfile.profile.simple._

  private lazy val dao = AnnotationsDAO()

  "The Annotations DAO" - {

    "should be able to perform operations in the Annotations table" - {

      "insert a new Annotation" in {
        Documents += Document(None, "title", "text")
        Keywords  += Keyword(None, "keyword", Drug)

        dao save Annotation(None, 1, 1, "text", 0, 4)

        Annotations.list should have size 1
        Annotations.first should have (
          'id       (Some(AnnotationId(1))),
          'docId    (DocumentId(1)),
          'keyId    (KeywordId(1)),
          'text     (Sentence("text")),
          'startPos (0),
          'endPos   (4)
        )
      }

      "update an existing Annotation" in {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotations.first

        dao save annotation.copy(text = "text updated")

        Annotations.list should have size 1
        Annotations.first should have (
          'id       (Some(AnnotationId(1))),
          'docId    (DocumentId(1)),
          'keyId    (KeywordId(1)),
          'text     (Sentence("text updated")),
          'startPos (0),
          'endPos   (4)
        )
      }

      "delete an existing Annotation" in {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotations.first

        dao delete annotation

        Annotations.list should be ('empty)
      }

      "check if it contains an Annotation" in {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotations.first

        (dao contains annotation) should be (true)
      }

      "find an existing Annotation by its ID" in {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val id = (Annotations map (_.id)).first

        (dao findById id) should be ('defined)
        (dao findById id).value should have (
          'id       (Some(AnnotationId(1))),
          'docId    (DocumentId(1)),
          'keyId    (KeywordId(1)),
          'text     (Sentence("text")),
          'startPos (0),
          'endPos   (4)
        )

        (dao findById Some(id)) should be ('defined)
        (dao findById Some(id)).value should have (
          'id       (Some(AnnotationId(1))),
          'docId    (DocumentId(1)),
          'keyId    (KeywordId(1)),
          'text     (Sentence("text")),
          'startPos (0),
          'endPos   (4)
        )
      }

    }

  }

}

