package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.{ Actor, PoisonPill, Props }

import play.api.db.slick.DB
import play.api.test._
import play.api.test.Helpers._

import org.scalatest.prop.TableDrivenPropertyChecks._

import es.uvigo.esei.tfg.smartdrugsearch.model._
import es.uvigo.esei.tfg.smartdrugsearch.model.database.DAL
import Category._

private[annotator] trait OscarSpecSetup extends AnnotatorBaseSpec {

  protected val expectations = Table(
    ("document", "entities", "annotations"),
    (
      Document(Some(1), "empty document", ""),
      Seq.empty,
      Seq.empty
    ),
    (
      Document(Some(1), "one-word document", "methylethane"),
      Seq(NamedEntity(Some(1), "InChI=1/C3H8/c1-3-2/h3H2,1-2H3", Compound, 1)),
      Seq(Annotation(Some(1), 1, 1, "methylethane", 0, 12))
    ),
    (
      Document(Some(1), "one-phrase document", "Then we mix benzene with napthyridine and toluene."),
      Seq(
        NamedEntity(Some(1), "InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H", Compound, 1),
        NamedEntity(Some(2), "InChI=1/C7H8/c1-7-5-3-2-4-6-7/h2-6H,1H3", Compound, 1)
      ),
      Seq(
        Annotation(Some(1), 1, 1, "benzene", 12, 19),
        Annotation(Some(2), 1, 2, "toluene", 42, 48)
      )
    )
  )

}

class OscarNERAdapterSpec extends OscarSpecSetup {

  "The Oscar Annotator" - {

    "should be able to annotate chemical compounds in Documents" in new WithApplication {
      val dal = DAL(DB("test").driver)

      import dal._
      import dal.profile.simple._

      DB("test") withSession { implicit session : Session =>
        forAll (expectations) { (document, entities, annotations) =>

          info(s"checking validity of annotations for document '${document.title}'")
          dal.create

          val oscar = system.actorOf(Props(classOf[OscarNERAdapter], dal))
          oscar ! session

          Documents += document

          oscar ! AnnotateDocument(document)
          expectMsg(10.seconds, FinishedAnnotation(document))

          NamedEntities.list should contain theSameElementsAs (entities)
          Annotations.list   should contain theSameElementsAs (annotations)

          oscar ! PoisonPill
          dal.drop

        }
      }
    }

  }

}

