package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseBaseSpec

class KeywordsDAOSpec extends DatabaseBaseSpec {

  import dbProfile.Keywords
  import dbProfile.profile.simple._

  private lazy val dao = KeywordsDAO()

  "The Keywords DAO" - {

    "should be able to perform operations in the Keywords table" - {

      "insert a new Keyword" in {
        dao save Keyword(None, "keyword", Drug)

        Keywords.list should have size 1
        Keywords.first should have (
          'id          (Some(KeywordId(1))),
          'normalized  (Sentence("keyword")),
          'category    (Drug),
          'occurrences (0)
        )
      }

      "update an existing Keyword" in {
        Keywords += Keyword(None, "keyword", Species)
        val keyword = Keywords.first

        dao save keyword.copy(occurrences = 7)

        Keywords.list should have size 1
        Keywords.first should have (
          'id          (Some(KeywordId(1))),
          'normalized  (Sentence("keyword")),
          'category    (Species),
          'occurrences (7)
        )
      }

      "delete an existing Keyword" in {
        Keywords += Keyword(None, "keyword", Drug, 3)
        val keyword = Keywords.first

        dao delete keyword

        Keywords.list should be ('empty)
      }

      "check if it contains a Keyword" in {
        Keywords += Keyword(None, "keyword", Compound)
        val keyword = Keywords.first

        (dao contains keyword) should be (true)
      }

      "find an existing Keyword by its ID" in {
        Keywords += Keyword(None, "keyword", Species, 9)
        val id = (Keywords map (_.id)).first

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
      }

      "find an existing Keyword by its normalized sentence" in {
        val normalized = Sentence("normalized keyword")
        Keywords += Keyword(None, normalized, Compound)

        (dao findByNormalized normalized) should be ('defined)
        (dao findByNormalized normalized).value should have (
          'id          (Some(KeywordId(1))),
          'normalized  (Sentence("normalized keyword")),
          'category    (Compound),
          'occurrences (0)
        )
      }

    }

  }

}

