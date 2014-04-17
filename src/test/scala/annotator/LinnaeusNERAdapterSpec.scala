package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.{ Actor, PoisonPill, Props }

import play.api.db.slick.DB
import play.api.test._

import org.scalatest.prop.TableDrivenPropertyChecks._

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

private[annotator] trait LinnaeusSpecSetup extends AnnotatorBaseSpec {

  protected def cleanSpaces(str : String) : String =
    str.stripMargin filter (_ >= ' ')

  protected val expectations = Table(
    ("document", "keywords", "annotations"),
    (
      Document(Some(1), "empty document", " "),
      Seq.empty,
      Seq.empty
    ),
    (
     Document(Some(1), "one-word document", "human"),
     Seq(Keyword(Some(1), "homo sapiens", Species, 1)),
     Seq(Annotation(Some(1), 1, 1, "human", 0, 5))
    ),
    (
      Document(Some(1), "test document", cleanSpaces(
        """|We have boy a hub identified IGF022/01 cellline Buchnera aphidicola 
           |a novel human cDNA with a predicted protein sequence that has 28% 
           |amin acid identity with the E. coli Hsp70 co-chaperone GrpE and 
           |designated it HMGE"""
      )),
      Seq(
        Keyword(Some(1), "homo sapiens", Species, 3),
        Keyword(Some(2), "escherichia coli", Species, 1)
      ),
      Seq(
        Annotation(Some(1), 1, 1, "boy", 8, 11),
        Annotation(Some(2), 1, 1, "igf022/01 cellline", 29, 47),
        Annotation(Some(3), 1, 1, "human", 76, 81),
        Annotation(Some(4), 1, 2, "e. coli", 162, 169)
      )
    )
  )

}

class LinnaeusNERAdapterSpec extends LinnaeusSpecSetup {

  "The Linnaeus Annotator" - {

    "should be able to annotate species in a Document" in new WithApplication {
      DatabaseProfile setProfile DB("test").driver

      val db = DatabaseProfile()
      import db.profile.simple._

      val Documents   = TableQuery[db.DocumentsTable]
      val Keywords    = TableQuery[db.KeywordsTable]
      val Annotations = TableQuery[db.AnnotationsTable]

      DB("test") withSession { implicit session =>
        forAll (expectations) { (document, keywords, annotations) =>
          info(s"checking validity of annotations for Document '${document.title}'")
          db.create

          val linnaeus = system.actorOf(Props[LinnaeusNERAdapter])
          linnaeus ! session

          Documents += document

          linnaeus ! Annotate(document)
          expectMsg(10.seconds, Finished(document))
          linnaeus ! PoisonPill

          Keywords.list    should contain theSameElementsAs (keywords)
          Annotations.list should contain theSameElementsAs (annotations)

          db.drop
        }
      }
    }

  }

}

