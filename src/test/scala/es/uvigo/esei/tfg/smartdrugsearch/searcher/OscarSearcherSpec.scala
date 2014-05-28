package es.uvigo.esei.tfg.smartdrugsearch.searcher

import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

class OscarSearcherSpec extends BaseSpec {

  private[this] lazy val expectations = Table(
    ("document", "keywords", "annotations", "searchTerms"),
    (
      Document(Some(1), "empty document", " "),
      Set.empty[Keyword],
      Set.empty[Annotation],
      Sentence("empty search")
    ),
    (
      Document(Some(1), "test document", "Then we mix benzene with napthyridine and toluene"),
      Set(
        Keyword(Some(1), "InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H",       Compound, 1),
        Keyword(Some(2), "InChI=1/C7H8/c1-7-5-3-2-4-6-7/h2-6H,1H3", Compound, 1)
      ),
      Set(
        Annotation(Some(1), 1, 1, "benzene", 12, 19),
        Annotation(Some(2), 1, 2, "toluene", 42, 49)
      ),
      Sentence("cyclohexa-1,3,5-triene and methylbenzene")
    )
  )

  "The Oscar Searcher" - {

    forAll(expectations) { (document, keywords, annotations, searchTerms) =>

      s"should return the correct set of keywords when searching '${searchTerms}'" in new WithApplication {
        val searcher = OscarSearcher()
        val database = DatabaseProfile()
        implicit val session = database.createSession()

        import database._
        import database.profile.simple._

        Documents    += document
        Keywords    ++= keywords
        Annotations ++= annotations

        whenReady(searcher search searchTerms) {
          _ should contain theSameElementsAs keywords
        }
      }

    }

  }

}

