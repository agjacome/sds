package es.uvigo.ei.sing.sds.annotator

import akka.actor.{ PoisonPill, Props }
import play.api.test.WithApplication

import es.uvigo.ei.sing.sds.ActorBaseSpec
import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.database.DatabaseProfile

class OscarAnnotatorSpec extends ActorBaseSpec {

  private[this] lazy val expectations = Table(
    ("document", "keywords", "annotations"),
    (("empty document", " "), Set.empty, Set.empty),
    (
      ("one-word document", "methylethane"),
      Set((Sentence("InChI=1S/C3H8/c1-3-2/h3H2,1-2H3"), Compound, Size(1))),
      Set((Sentence("methylethane"), Position(0), Position(12)))
    ),
    (
      ("one-phrase document", "Then we mix benzene with napthyridine and toluene."),
      Set(
        (Sentence("InChI=1S/C6H6/c1-2-4-6-5-3-1/h1-6H"),       Compound, Size(1)),
        (Sentence("InChI=1S/C7H8/c1-7-5-3-2-4-6-7/h2-6H,1H3"), Compound, Size(1))
      ),
      Set(
        (Sentence("benzene"), Position(12), Position(19)),
        (Sentence("toluene"), Position(42), Position(49))
      )
    )
  )

  "The Oscar Annotator" - {

    forAll (expectations) { (document, keywords, annotations) =>

      s"should annotate chemical Compounds in Document '${document._1}'" in new WithApplication {
        val oscar    = system.actorOf(Props[OscarAnnotator], "Oscar")
        val database = DatabaseProfile()
        implicit val session = database.createSession()

        import database._
        import database.profile.simple._

        val documentId = Documents returning Documents.map(_.id) += Document(
          None, document._1, document._2
        )

        oscar ! Annotate(documentId)
        expectMsg(waitTime, Finished(documentId))
        oscar ! PoisonPill

        val storedKeywords = (Keywords map { k => (k.normalized, k.category, k.occurrences) }).list
        storedKeywords should contain theSameElementsAs keywords

        val storedAnnotations = (Annotations map { a => (a.text, a.startPosition, a.endPosition) }).list
        storedAnnotations should contain theSameElementsAs annotations

        session.close()
      }

    }

  }

}

