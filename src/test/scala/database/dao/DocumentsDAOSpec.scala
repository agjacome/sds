package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import play.api.db.slick.DB
import play.api.test._

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

class DocumentsDAOSpec extends BaseSpec {

  "The Documents DAO" - {

    "should be able to perform operations in the Documents table" - {

      "insert a new Document" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents = TableQuery[db.DocumentsTable]
        DB("test") withSession { implicit session =>
          db.create

          val dao = DocumentsDAO()
          dao save Document(None, "title", "text")

          documents.list should have size 1
          documents.first should have (
            'id    (Some(DocumentId(1))),
            'title (Sentence("title")),
            'text  ("text")
          )

          db.drop
        }
      }

      "update an existing Document" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents = TableQuery[db.DocumentsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents += Document(None, "my title", "my text")
          val document = documents.first

          val dao = DocumentsDAO()
          dao save document.copy(title = "my updated title", text = "my updated text")

          documents.list should have size 1
          documents.first should have (
            'id    (Some(DocumentId(1))),
            'title (Sentence("my updated title")),
            'text  ("my updated text")
          )

          db.drop
        }
      }

      "delete an existing Document" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents = TableQuery[db.DocumentsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents += Document(None, "title", "text")
          val document = documents.first

          val dao = DocumentsDAO()
          dao delete document

          documents.list should be ('empty)

          db.drop
        }
      }

      "check if it contains a Document" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents = TableQuery[db.DocumentsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents += Document(None, "title", "text")
          val document = documents.first

          val dao = DocumentsDAO()
          (dao contains document) should be (true)

          db.drop
        }
      }

      "find an existing Document by its ID" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents = TableQuery[db.DocumentsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents += Document(None, "title", "text")
          val id = (documents map (_.id)).first

          val dao = DocumentsDAO()

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

          db.drop
        }
      }

    }

  }

}
