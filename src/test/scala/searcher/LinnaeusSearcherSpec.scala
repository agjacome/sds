package es.uvigo.ei.sing.sds.searcher

import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.WithApplication

import es.uvigo.ei.sing.sds.BaseSpec
import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity._

class LinnaeusSearcherSpec extends BaseSpec {

  private[this] lazy val expectations = Table(
    ("document", "keywords", "annotations", "searchTerms"),
    (
      Document(Some(1), "empty document", " "),
      Set.empty[Keyword],
      Set.empty[Annotation],
      Sentence("empty search")
    ),
    (
      Document(Some(1), "test document", "boy e. coli"),
      Set(
        Keyword(Some(1), "Homo sapiens",     Species, 1),
        Keyword(Some(2), "Escherichia coli", Species, 1)
      ),
      Set(
        Annotation(Some(1), 1, 1, "boy",     0,  3),
        Annotation(Some(2), 1, 2, "e. coli", 4, 11)
      ),
      Sentence("human enterococcus coli")
    )
  )

  "The Linnaeus Searcher" - {

    forAll(expectations) { (document, keywords, annotations, searchTerms) =>

      s"should return the correct set of keywords when searching '${searchTerms}'" in new WithApplication {
        val searcher = LinnaeusSearcher()
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

