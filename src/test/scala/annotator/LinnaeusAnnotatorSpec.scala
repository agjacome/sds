package es.uvigo.ei.sing.sds.annotator

import akka.actor.{ PoisonPill, Props }
import play.api.test.WithApplication

import es.uvigo.ei.sing.sds.ActorBaseSpec
import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.database.DatabaseProfile

class LinnaeusAnnotatorSpec extends ActorBaseSpec {

  private[this] lazy val expectations = Table(
    ("document", "keywords", "annotations"),
    (("empty document", " "), Set.empty, Set.empty),
    (
      ("one-word document", "human"),
      Set((Sentence("homo sapiens"), Species, Size(1))),
      Set((Sentence("human"), Position(0), Position(5)))
    ),
    (
      ("test document", cleanSpaces(
        """|We have boy a hub identified IGF022/01 cellline Buchnera aphidicola 
           |a novel human cDNA with a predicted protein sequence that has 28% 
           |amin acid identity with the E. coli Hsp70 co-chaperone GrpE and 
           |designated it HMGE"""
      )),
      Set(
        (Sentence("homo sapiens"),     Species, Size(3)),
        (Sentence("escherichia coli"), Species, Size(1))
      ),
      Set(
        (Sentence("boy"),                Position(  8), Position( 11)),
        (Sentence("igf022/01 cellline"), Position( 29), Position( 47)),
        (Sentence("human"),              Position( 76), Position( 81)),
        (Sentence("e. coli"),            Position(162), Position(169))
      )
    )
  )

  "The Linnaeus Annotator" - {

    forAll (expectations) { (document, keywords, annotations) =>

      s"should annotate Species in Document '${document._1}'" in new WithApplication {
        val linnaeus = system.actorOf(Props[LinnaeusAnnotator], "Linnaeus")
        val database = DatabaseProfile()
        implicit val session = database.createSession()

        import database._
        import database.profile.simple._

        val documentId = Documents returning Documents.map(_.id) += Document(
          None, document._1, document._2
        )

        linnaeus ! Annotate(documentId)
        expectMsg(waitTime, Finished(documentId))
        linnaeus ! PoisonPill

        val storedKeywords = (Keywords map { k => (k.normalized, k.category, k.occurrences) }).list
        storedKeywords should contain theSameElementsAs keywords

        val storedAnnotations = (Annotations map { a => (a.text, a.startPosition, a.endPosition) }).list
        storedAnnotations should contain theSameElementsAs annotations

        session.close()
      }

    }

  }

}
