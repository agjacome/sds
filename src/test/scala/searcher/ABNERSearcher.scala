package es.uvigo.ei.sing.sds.searcher

import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.WithApplication

import es.uvigo.ei.sing.sds.BaseSpec
import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity._

class ABNERSearcherSpec extends BaseSpec {

  private[this] lazy val expectations = Table(
    ("document", "keywords", "annotations", "searchTerms"),
    (
      Document(Some(1), "empty document", " "),
      Set.empty[Keyword],
      Set.empty[Annotation],
      Sentence("empty search")
    ),
    (
      Document(Some(1), "test document", cleanSpaces(
        """|We have identified a transcriptional repressor, Nrg1, in a genetic 
           |screen designed to reveal negative factors involved in the 
           |expression of STA1, which encodes a glucoamylase. The NRG1 gene 
           |encodes a 25-kDa C2H2 zinc finger protein which specifically binds 
           |to two regions in the upstream activation sequence of the  STA1 
           |gene, as judged by gel retardation DNase I footprint analyses. 
           |Disruption of the NRG1 gene causes a fivefold increase in the 
           |level of the STA1 transcript in the presence of glucose."""
      )),
      Set(
        Keyword(Some(1), "nrg1", Protein, 1),
        Keyword(Some(2), "sta1", Protein, 1)
      ),
      Set(
        Annotation(Some(1), 1, 1, "Nrg1",  48,  52),
        Annotation(Some(2), 1, 2, "STA1", 140, 144)
      ),
      Sentence("transcriptional repressor, NRG1, in the expression of STA1")
    )
  )

  "The ABNER Searcher" - {

    forAll(expectations) { (document, keywords, annotations, searchTerms) =>

      s"should return the correct set of keywords when searching '${searchTerms}'" in new WithApplication {
        val searcher = ABNERSearcher()
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

