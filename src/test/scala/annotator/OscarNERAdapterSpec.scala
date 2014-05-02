package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.duration._
import akka.actor.{ PoisonPill, Props }

import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[annotator] trait OscarSpecSetup extends AnnotatorBaseSpec {

  protected val expectations = Table(
    ("document", "keywords", "annotations"),
    (
      Document(Some(1), "empty document", " "),
      Seq.empty,
      Seq.empty
    ),
    (
      Document(Some(1), "one-word document", "methylethane"),
      Seq(Keyword(Some(1), "InChI=1/C3H8/c1-3-2/h3H2,1-2H3", Compound, 1)),
      Seq(Annotation(Some(1), 1, 1, "methylethane", 0, 12))
    ),
    (
      Document(Some(1), "one-phrase document", "Then we mix benzene with napthyridine and toluene."),
      Seq(
        Keyword(Some(1), "InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H", Compound, 1),
        Keyword(Some(2), "InChI=1/C7H8/c1-7-5-3-2-4-6-7/h2-6H,1H3", Compound, 1)
      ),
      Seq(
        Annotation(Some(1), 1, 1, "benzene", 12, 19),
        Annotation(Some(2), 1, 2, "toluene", 42, 49)
      )
    )
  )

}

class OscarNERAdapterSpec extends OscarSpecSetup {

  import dbProfile.{ Annotations, Documents, Keywords }
  import dbProfile.profile.simple._

  "The Oscar Annotator" - {

    "should be able to annotate chemical compounds in Documents" - {

      forAll (expectations) { (document, keywords, annotations) =>
        s"checking validity of annotations for Document '${document.title}'" in new WithApplication {
          Documents += document
          val oscar = system.actorOf(Props[OscarNERAdapter])

          // OSCAR takes a while to start (and OscarNERAdapter should
          // initialize it lazily), 10 seconds is probably enough to receive a
          // valid response
          oscar ! Annotate(document)
          expectMsg(10.seconds, Finished(document))
          oscar ! PoisonPill

          Keywords.list    should contain theSameElementsAs keywords
          Annotations.list should contain theSameElementsAs annotations
        }
      }

    }

  }

}

