package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseBaseSpec

class AnnotationsDAOSpec extends DatabaseBaseSpec {

  import dbProfile.{ Annotations, Documents, Keywords }
  import dbProfile.profile.simple._

  "The Annotations DAO" - {

    "should be able to perform operations in the Annotations table" - {

      "insert a new Annotation" in new WithApplication {
        Documents += Document(None, "title", "text")
        Keywords  += Keyword(None, "keyword", Drug)

        AnnotationsDAO() save Annotation(None, 1, 1, "text", 0, 4)

        Annotations.list should have size 1
        Annotations.first should have (
          'id       (Some(AnnotationId(1))),
          'docId    (1),
          'keyId    (1),
          'text     (Sentence("text")),
          'startPos (0),
          'endPos   (4)
        )
      }

      "update an existing Annotation" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotations.first

        AnnotationsDAO() save annotation.copy(text = "text updated")

        Annotations.list should have size 1
        Annotations.first should have (
          'id       (Some(AnnotationId(1))),
          'docId    (1),
          'keyId    (1),
          'text     (Sentence("text updated")),
          'startPos (0),
          'endPos   (4)
        )
      }

      "delete an existing Annotation" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotations.first

        AnnotationsDAO() delete annotation

        Annotations.list should be ('empty)
      }

      "check if it contains an Annotation" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotations.first

        (AnnotationsDAO() contains annotation) should be (true)
      }

      "find an existing Annotation by its ID" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val id = (Annotations map (_.id)).first

        val dao = AnnotationsDAO()

        (dao findById id) should be ('defined)
        (dao findById id).value should have (
          'id       (Some(AnnotationId(1))),
          'docId    (1),
          'keyId    (1),
          'text     (Sentence("text")),
          'startPos (0),
          'endPos   (4)
        )

        (dao findById Some(id)) should be ('defined)
        (dao findById Some(id)).value should have (
          'id       (Some(AnnotationId(1))),
          'docId    (1),
          'keyId    (1),
          'text     (Sentence("text")),
          'startPos (0),
          'endPos   (4)
        )
      }

    }

    "should be able to perform operations in the Annotations Foreign Keys" - {

      "get the referenced Document" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotation(Some(1), 1, 1, "text", 0, 4)

        val dao = AnnotationsDAO()

        (dao documentFor annotation) should be ('defined)
        (dao documentFor annotation).value should have (
          'id    (Some(DocumentId(1))),
          'title (Sentence("title")),
          'text  ("text")
        )

        (dao documentFor annotation.id) should be ('defined)
        (dao documentFor annotation.id).value should have (
          'id    (Some(DocumentId(1))),
          'title (Sentence("title")),
          'text  ("text")
        )

        (dao documentFor annotation.id.get) should be ('defined)
        (dao documentFor annotation.id.get).value should have (
          'id    (Some(DocumentId(1))),
          'title (Sentence("title")),
          'text  ("text")
        )
      }

      "get the referenced Keyword" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text", 0, 4)
        val annotation = Annotation(Some(1), 1, 1, "text", 0, 4)

        val dao = AnnotationsDAO()

        (dao keywordFor annotation) should be ('defined)
        (dao keywordFor annotation).value should have (
          'id          (Some(KeywordId(1))),
          'normalized  (Sentence("keyword")),
          'category    (Drug),
          'occurrences (0)
        )

        (dao keywordFor annotation.id) should be ('defined)
        (dao keywordFor annotation.id).value should have (
          'id          (Some(KeywordId(1))),
          'normalized  (Sentence("keyword")),
          'category    (Drug),
          'occurrences (0)
        )

        (dao keywordFor annotation.id.get) should be ('defined)
        (dao keywordFor annotation.id.get).value should have (
          'id          (Some(KeywordId(1))),
          'normalized  (Sentence("keyword")),
          'category    (Drug),
          'occurrences (0)
        )
      }

      "find which Annotations reference a given Document" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text1", 0, 4)
        Annotations += Annotation(None, 1, 1, "text2", 0, 4)
        val document = Document(Some(1), "title", "text")

        val dao = AnnotationsDAO()

        (dao findByDocument document) should have size 2
        (dao findByDocument document) should contain theSameElementsAs Seq(
          Annotation(Some(1), 1, 1, "text1", 0, 4),
          Annotation(Some(2), 1, 1, "text2", 0, 4)
        )

        (dao findByDocumentId document.id) should have size 2
        (dao findByDocumentId document.id) should contain theSameElementsAs Seq(
          Annotation(Some(1), 1, 1, "text1", 0, 4),
          Annotation(Some(2), 1, 1, "text2", 0, 4)
        )

        (dao findByDocumentId document.id.get) should have size 2
        (dao findByDocumentId document.id.get) should contain theSameElementsAs Seq(
          Annotation(Some(1), 1, 1, "text1", 0, 4),
          Annotation(Some(2), 1, 1, "text2", 0, 4)
        )
      }

      "find which Annotations reference a given Keyword" in new WithApplication {
        Documents   += Document(None, "title", "text")
        Keywords    += Keyword(None, "keyword", Drug)
        Annotations += Annotation(None, 1, 1, "text1", 0, 4)
        Annotations += Annotation(None, 1, 1, "text2", 0, 4)
        val keyword = Keyword(Some(1), "keyword", Drug)

        val dao = AnnotationsDAO()

        (dao findByKeyword keyword) should have size 2
        (dao findByKeyword keyword) should contain theSameElementsAs Seq(
          Annotation(Some(1), 1, 1, "text1", 0, 4),
          Annotation(Some(2), 1, 1, "text2", 0, 4)
        )

        (dao findByKeywordId keyword.id) should have size 2
        (dao findByKeywordId keyword.id) should contain theSameElementsAs Seq(
          Annotation(Some(1), 1, 1, "text1", 0, 4),
          Annotation(Some(2), 1, 1, "text2", 0, 4)
        )

        (dao findByKeywordId keyword.id.get) should have size 2
        (dao findByKeywordId keyword.id.get) should contain theSameElementsAs Seq(
          Annotation(Some(1), 1, 1, "text1", 0, 4),
          Annotation(Some(2), 1, 1, "text2", 0, 4)
        )
      }

    }

  }

}

