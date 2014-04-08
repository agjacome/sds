package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import play.api.db.slick.DB
import play.api.test._

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

class AnnotationsDAOSpec extends BaseSpec {

  "The Annotations DAO" - {

    "should be able to perform operations in the Annotations table" - {

      "insert a new Annotation" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents   = TableQuery[db.DocumentsTable]
        val keywords    = TableQuery[db.KeywordsTable]
        val annotations = TableQuery[db.AnnotationsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents += Document(None, "title", "text")
          keywords  += Keyword(None, "keyword", Drug)

          val dao = AnnotationsDAO()
          dao save Annotation(None, 1, 1, "text", 0, 4)

          annotations.list should have size 1
          annotations.first should have (
            'id       (Some(AnnotationId(1))),
            'docId    (DocumentId(1)),
            'keyId    (KeywordId(1)),
            'text     (Sentence("text")),
            'startPos (0),
            'endPos   (4)
          )

          db.drop
        }
      }

      "update an existing Annotation" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents   = TableQuery[db.DocumentsTable]
        val keywords    = TableQuery[db.KeywordsTable]
        val annotations = TableQuery[db.AnnotationsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents   += Document(None, "title", "text")
          keywords    += Keyword(None, "keyword", Drug)
          annotations += Annotation(None, 1, 1, "text", 0, 4)
          val annotation = annotations.first

          val dao = AnnotationsDAO()
          dao save annotation.copy(text = "text updated")

          annotations.list should have size 1
          annotations.first should have (
            'id       (Some(AnnotationId(1))),
            'docId    (DocumentId(1)),
            'keyId    (KeywordId(1)),
            'text     (Sentence("text updated")),
            'startPos (0),
            'endPos   (4)
          )

          db.drop
        }
      }

      "delete an existing Annotation" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents   = TableQuery[db.DocumentsTable]
        val keywords    = TableQuery[db.KeywordsTable]
        val annotations = TableQuery[db.AnnotationsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents   += Document(None, "title", "text")
          keywords    += Keyword(None, "keyword", Drug)
          annotations += Annotation(None, 1, 1, "text", 0, 4)
          val annotation = annotations.first

          val dao = AnnotationsDAO()
          dao delete annotation

          annotations.list should be ('empty)

          db.drop
        }
      }

      "check if it contains an Annotation" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents   = TableQuery[db.DocumentsTable]
        val keywords    = TableQuery[db.KeywordsTable]
        val annotations = TableQuery[db.AnnotationsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents   += Document(None, "title", "text")
          keywords    += Keyword(None, "keyword", Drug)
          annotations += Annotation(None, 1, 1, "text", 0, 4)
          val annotation = annotations.first

          val dao = AnnotationsDAO()
          (dao contains annotation) should be (true)

          db.drop
        }
      }

      "find an existing Annotation by its ID" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val documents   = TableQuery[db.DocumentsTable]
        val keywords    = TableQuery[db.KeywordsTable]
        val annotations = TableQuery[db.AnnotationsTable]
        DB("test") withSession { implicit session =>
          db.create

          documents   += Document(None, "title", "text")
          keywords    += Keyword(None, "keyword", Drug)
          annotations += Annotation(None, 1, 1, "text", 0, 4)
          val id = (annotations map (_.id)).first

          val dao = AnnotationsDAO()

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

          db.drop
        }
      }

    }

  }

}

