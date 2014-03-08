package es.uvigo.esei.tfg.smartdrugsearch.model.database

import play.api.Application
import play.api.db.slick.DB
import play.api.test._
import play.api.test.Helpers._

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.model._

class DALSpec extends BaseSpec {

  "The Data Access Layer" - {

    "should work with the Database as expected" - {

      "CRUDing documents" in new WithApplication {
        val dal = DAL(DB("test").driver)

        import dal._
        import dal.profile.simple._

        DB("test") withSession { implicit session : Session =>
          info("creating documents table")
          Documents.ddl.create

          info("inserting documents")
          Documents ++= Seq(
            Document(None, "Title One",   "Text One"),
            Document(None, "Title Two",   "Text Two"),
            Document(None, "Title Three", "Text Three")
          )

          info("retrieving all documents")
          Documents.list should have size (3)
          all (Documents.list map (_.id)) should be ('defined)

          info("searching for a concrete document")
          val second = (Documents filter (_.title is Sentence("Title Two"))).first
          second should have (
            'id    (Some(DocumentId(2))),
            'title (Sentence("Title Two")),
            'text  ("Text Two")
          )

          info("updating a document's information")
          (for {
            doc <- Documents
            if doc.title is Sentence("Title Three")
          } yield (doc.text)) update "The new and shiny third text"

          val third = (Documents filter (_.id is DocumentId(3))).first
          third.text should equal ("The new and shiny third text")

          info("deleting a document")
          (Documents filter (_.id is DocumentId(1))).delete
          (Documents filter (_.text is "Text One")).firstOption should be (None)

          info("removing documents table")
          Documents.ddl.drop
        }
      }

      "CRUDing named entities" in (pending)

      "CRUDing annotations" in (pending)

    }

  }

}

