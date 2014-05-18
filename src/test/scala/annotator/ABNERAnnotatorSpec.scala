package es.uvigo.esei.tfg.smartdrugsearch.annotator

import akka.actor.{ PoisonPill, Props }
import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.ActorBaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

class ABNERAnnotatorSpec extends ActorBaseSpec {

  private[this] lazy val expectations = Table(
    ("document", "keywords", "annotations"),
    (("empty document", " "), Set.empty, Set.empty),
    (
      ("test document", cleanSpaces(
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
        (Sentence("transcriptional repressor"),    Protein, Size(1)),
        (Sentence("Nrg1"),                         Protein, Size(1)),
        (Sentence("reveal negative factors"),      Protein, Size(1)),
        (Sentence("STA1"),                         Protein, Size(1)),
        (Sentence("glucoamylase"),                 Protein, Size(1)),
        (Sentence("NRG1 gene"),                    DNA,     Size(2)),
        (Sentence("C2H2 zinc finger protein"),     Protein, Size(1)),
        (Sentence("upstream activation sequence"), DNA,     Size(1)),
        (Sentence("STA1 gene"),                    DNA,     Size(1)),
        (Sentence("DNase I"),                      Protein, Size(1)),
        (Sentence("STA1 transcript"),              RNA,     Size(1))
      ),
      Set(
        (Sentence("transcriptional repressor"),    Position( 21), Position( 46)),
        (Sentence("Nrg1"),                         Position( 48), Position( 52)),
        (Sentence("reveal negative factors"),      Position( 86), Position(109)),
        (Sentence("STA1"),                         Position(140), Position(144)),
        (Sentence("glucoamylase"),                 Position(162), Position(174)),
        (Sentence("NRG1 gene"),                    Position(180), Position(189)),
        (Sentence("C2H2 zinc finger protein"),     Position(207), Position(231)),
        (Sentence("upstream activation sequence"), Position(279), Position(307)),
        (Sentence("STA1 gene"),                    Position(316), Position(325)),
        (Sentence("DNase I"),                      Position(356), Position(363)),
        (Sentence("NRG1 gene"),                    Position(402), Position(411)),
        (Sentence("STA1 transcript"),              Position(459), Position(474))
      )
    )
  )

  "The ABNER Annotator" - {

    forAll (expectations) { (document, keywords, annotations) =>

      s"should annotate Proteins correctly in Document '${document._1}'" in new WithApplication {
        val abner    = system.actorOf(Props[ABNERAnnotator], "ABNER")
        val database = DatabaseProfile()
        implicit val session = database.createSession()

        import database._
        import database.profile.simple._

        val documentId = Documents returning Documents.map(_.id) += Document(
          None, document._1, document._2
        )

        abner ! Annotate(documentId)
        expectMsg(waitTime, Finished(documentId))
        abner ! PoisonPill

        val storedKeywords = (Keywords map { k => (k.normalized, k.category, k.occurrences) }).list
        storedKeywords should contain theSameElementsAs keywords

        val storedAnnotations = (Annotations map { a => (a.text, a.startPosition, a.endPosition) }).list
        storedAnnotations should contain theSameElementsAs annotations

        session.close()
      }

    }

  }

}

