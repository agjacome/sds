package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

import scala.concurrent.duration._

import akka.actor.{ Actor, ActorSystem, PoisonPill, Props }
import akka.testkit.TestKitBase

import play.api.db.slick.DB
import play.api.test._
import play.api.test.Helpers._

import org.scalatest.BeforeAndAfterAll
import org.scalatest.prop.TableDrivenPropertyChecks
import TableDrivenPropertyChecks._

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.model._
import es.uvigo.esei.tfg.smartdrugsearch.model.database.DAL
import Category.{ Category, Species }

class LinnaeusNERAdapterSpec extends BaseSpec with TestKitBase with BeforeAndAfterAll with TableDrivenPropertyChecks {

  implicit lazy val system = ActorSystem()

  override def afterAll : Unit =
    shutdown(system)

  private def cleanSpaces(str : String) : String =
    str.stripMargin filter (_ >= ' ')

  // TODO: add more expectations, tuples (document, entities, annotations),
  // test correct behavior of the Linnaeus Annotator
  private val expectations = Table(
    ("document", "entities", "annotations"),
    (
      Document(Some(1), "Test Document One", cleanSpaces(
        """|We have boy a hub identified IGF022/01 cellline Buchnera aphidicola 
           |a novel human cDNA with a predicted protein sequence that has 28% 
           |amin acid identity with the E. coli Hsp70 co-chaperone GrpE and 
           |designated it HMGE"""
      )),
      Seq(
        NamedEntity(Some(1), "homo sapiens", Species, 3),
        NamedEntity(Some(2), "escherichia coli", Species, 1)
      ),
      Seq(
        Annotation(Some(1), 1, 1, "boy", 8, 11),
        Annotation(Some(2), 1, 1, "igf022/01 cellline", 29, 47),
        Annotation(Some(3), 1, 1, "human", 76, 81),
        Annotation(Some(4), 1, 2, "e. coli", 162, 169)
      )
    )
  )


  "The Linnaeus Annotator" - {

    "should be able to annotate species in Documents" in new WithApplication {
      val dal = DAL(DB("test").driver)

      import dal._
      import dal.profile.simple._

      DB("test") withSession { implicit session : Session =>

        info("creating Linnaeus Actor")
        val linnaeus = system.actorOf(Props(classOf[LinnaeusNERAdapter], dal))
        linnaeus ! session

        info("checking validity of annotations performed by Linnaeus Actor")
        forAll (expectations) { (document, entities, annotations) =>
          dal.create
          Documents += document

          linnaeus ! document
          expectNoMsg(5.seconds)

          NamedEntities.list should contain theSameElementsAs (entities)
          Annotations.list   should contain theSameElementsAs (annotations)

          dal.drop
        }

        info("shutting down Linnaeus Actor")
        linnaeus ! PoisonPill

      }
    }

  }

}

