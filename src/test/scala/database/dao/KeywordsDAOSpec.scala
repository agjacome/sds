package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import play.api.db.slick.DB
import play.api.test._

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

class KeywordsDAOSpec extends BaseSpec {

  "The Keywords DAO" - {

    "should be able to perform operations in the Keywords table" - {

      "insert a new Keyword" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val keywords = TableQuery[db.KeywordsTable]
        DB("test") withSession { implicit session =>
          db.create

          val dao = KeywordsDAO()
          dao save Keyword(None, "keyword", Drug)

          keywords.list should have size 1
          keywords.first should have (
            'id          (Some(KeywordId(1))),
            'normalized  (Sentence("keyword")),
            'category    (Drug),
            'occurrences (0)
          )

          db.drop
        }
      }

      "update an existing Keyword" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val keywords = TableQuery[db.KeywordsTable]
        DB("test") withSession { implicit session =>
          db.create

          keywords += Keyword(None, "keyword", Species)
          val keyword = keywords.first

          val dao = KeywordsDAO()
          dao save keyword.copy(occurrences = 7)

          keywords.list should have size 1
          keywords.first should have (
            'id          (Some(KeywordId(1))),
            'normalized  (Sentence("keyword")),
            'category    (Species),
            'occurrences (7)
          )

          db.drop
        }
      }

      "delete an existing Keyword" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val keywords = TableQuery[db.KeywordsTable]
        DB("test") withSession { implicit session =>
          db.create

          keywords += Keyword(None, "keyword", Drug, 3)
          val keyword = keywords.first

          val dao = KeywordsDAO()
          dao delete keyword

          keywords.list should be ('empty)

          db.drop
        }
      }

      "check if it contains a Keyword" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val keywords = TableQuery[db.KeywordsTable]
        DB("test") withSession { implicit session =>
          db.create

          keywords += Keyword(None, "keyword", Compound)
          val keyword = keywords.first

          val dao = KeywordsDAO()
          (dao contains keyword) should be (true)

          db.drop
        }
      }

      "find an existing Keywod by its ID" in new WithApplication {
        DatabaseProfile setProfile DB("test").driver

        val db = DatabaseProfile()
        import db.profile.simple._

        val keywords = TableQuery[db.KeywordsTable]
        DB("test") withSession { implicit session =>
          db.create

          keywords += Keyword(None, "keyword", Species, 9)
          val id = (keywords map (_.id)).first

          val dao = KeywordsDAO()

          (dao findById id) should be ('defined)
          (dao findById id).value should have (
            'id          (Some(KeywordId(id))),
            'normalized  (Sentence("keyword")),
            'category    (Species),
            'occurrences (9)
          )

          (dao findById Some(id)) should be ('defined)
          (dao findById Some(id)).value should have (
            'id          (Some(KeywordId(id))),
            'normalized  (Sentence("keyword")),
            'category    (Species),
            'occurrences (9)
          )

          db.drop
        }
      }

    }

  }

}
