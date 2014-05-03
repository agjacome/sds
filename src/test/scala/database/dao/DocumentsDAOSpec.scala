package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseBaseSpec

class DocumentsDAOSpec extends DatabaseBaseSpec {

  import dbProfile.Documents
  import dbProfile.profile.simple._

  "The Documents DAO" - {

    "should be able to perform operations in the Documents table" - {

      "insert a new Document" in new WithApplication {
        DocumentsDAO() save Document(title = "title", text ="text")

        Documents.list should have size 1
        Documents.first should have (
          'id        (Some(DocumentId(1))),
          'title     (Sentence("title")),
          'text      ("text"),
          'annotated (false),
          'pubmedId  (None)
        )
      }

      "update an existing Document" in new WithApplication {
        Documents += Document(title = "my title", text = "my text")
        val document = Documents.first

        DocumentsDAO() save document.copy(title = "my updated title", text = "my updated text", annotated = true)

        Documents.list should have size 1
        Documents.first should have (
          'id        (Some(DocumentId(1))),
          'title     (Sentence("my updated title")),
          'text      ("my updated text"),
          'annotated (true),
          'pubmedId  (None)
        )
      }

      "delete an existing Document" in new WithApplication {
        Documents += Document(title = "title", text = "text")
        val document = Documents.first

        DocumentsDAO() delete document

        Documents.list should be ('empty)
      }

      "check if it contains a Document" in new WithApplication {
        Documents += Document(title = "title", text = "text")
        val document = Documents.first

        (DocumentsDAO() contains document) should be (true)
      }

      "find an existing Document by its ID" in new WithApplication {
        Documents += Document(title = "title", text = "text")
        val id = (Documents map (_.id)).first

        val dao = DocumentsDAO()

        (dao findById id) should be ('defined)
        (dao findById id).value should have (
          'id        (Some(DocumentId(id))),
          'title     (Sentence("title")),
          'text      ("text"),
          'annotated (false),
          'pubmedId  (None)
        )

        (dao findById Some(id)) should be ('defined)
        (dao findById Some(id)).value should have (
          'id        (Some(DocumentId(id))),
          'title     (Sentence("title")),
          'text      ("text"),
          'annotated (false),
          'pubmedId  (None)
        )
      }

      "find an existing document by its PumbedID" in new WithApplication {
        Documents += Document(title = "title", text = "text", pubmedId = Some(12))
        val id = PubMedId(12)

        val dao = DocumentsDAO()

        (dao findByPubMedId id) should be ('defined)
        (dao findByPubMedId id).value should have (
          'id        (Some(DocumentId(1))),
          'title     (Sentence("title")),
          'text      ("text"),
          'annotated (false),
          'pubmedId  (Some(id))
        )

        (dao findByPubMedId Some(id)) should be ('defined)
        (dao findByPubMedId Some(id)).value should have (
          'id        (Some(DocumentId(1))),
          'title     (Sentence("title")),
          'text      ("text"),
          'annotated (false),
          'pubmedId  (Some(id))
        )
      }

    }

  }

}

