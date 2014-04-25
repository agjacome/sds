package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseBaseSpec

class DocumentsDAOSpec extends DatabaseBaseSpec {

  import dbProfile.Documents
  import dbProfile.profile.simple._

  private lazy val dao = DocumentsDAO()

  "The Documents DAO" - {

    "should be able to perform operations in the Documents table" - {

      "insert a new Document" in {
        dao save Document(None, "title", "text")

        Documents.list should have size 1
        Documents.first should have (
          'id    (Some(DocumentId(1))),
          'title (Sentence("title")),
          'text  ("text")
        )
      }

      "update an existing Document" in {
        Documents += Document(None, "my title", "my text")
        val document = Documents.first

        dao save document.copy(title = "my updated title", text = "my updated text")

        Documents.list should have size 1
        Documents.first should have (
          'id    (Some(DocumentId(1))),
          'title (Sentence("my updated title")),
          'text  ("my updated text")
        )
      }

      "delete an existing Document" in {
        Documents += Document(None, "title", "text")
        val document = Documents.first

        dao delete document

        Documents.list should be ('empty)
      }

      "check if it contains a Document" in {
        Documents += Document(None, "title", "text")
        val document = Documents.first

        (dao contains document) should be (true)
      }

      "find an existing Document by its ID" in {
        Documents += Document(None, "title", "text")
        val id = (Documents map (_.id)).first

        (dao findById id) should be ('defined)
        (dao findById id).value should have (
          'id    (Some(DocumentId(id))),
          'title (Sentence("title")),
          'text  ("text")
        )

        (dao findById Some(id)) should be ('defined)
        (dao findById Some(id)).value should have (
          'id    (Some(DocumentId(id))),
          'title (Sentence("title")),
          'text  ("text")
        )
      }

    }

  }

}

