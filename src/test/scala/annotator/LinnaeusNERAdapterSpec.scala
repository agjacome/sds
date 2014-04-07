// package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

// import scala.concurrent.Future
// import scala.concurrent.duration._
// import akka.actor.{ Actor, PoisonPill, Props }

// import play.api.db.slick.DB
// import play.api.test._
// import play.api.test.Helpers._

// import org.scalatest.mock.MockitoSugar
// import org.scalatest.prop.TableDrivenPropertyChecks._

// import es.uvigo.esei.tfg.smartdrugsearch.model._
// import es.uvigo.esei.tfg.smartdrugsearch.model.database.DAL
// import Category._

// private[annotator] trait LinnaeusSpecSetup extends AnnotatorBaseSpec {

  // protected def cleanSpaces(str : String) : String =
    // str.stripMargin filter (_ >= ' ')

  // protected val expectations = Table(
    // ("document", "entities", "annotations"),
    // (
      // Document(Some(1), "empty document", ""),
      // Seq.empty,
      // Seq.empty
    // ),
    // (
      // Document(Some(1), "one-word document", "human"),
      // Seq(NamedEntity(Some(1), "homo sapiens", Species, 1)),
      // Seq(Annotation(Some(1), 1, 1, "human", 0, 5))
    // ),
    // (
      // Document(Some(1), "test document", cleanSpaces(
        // """|We have boy a hub identified IGF022/01 cellline Buchnera aphidicola 
           // |a novel human cDNA with a predicted protein sequence that has 28% 
           // |amin acid identity with the E. coli Hsp70 co-chaperone GrpE and 
           // |designated it HMGE"""
      // )),
      // Seq(
        // NamedEntity(Some(1), "homo sapiens", Species, 3),
        // NamedEntity(Some(2), "escherichia coli", Species, 1)
      // ),
      // Seq(
        // Annotation(Some(1), 1, 1, "boy", 8, 11),
        // Annotation(Some(2), 1, 1, "igf022/01 cellline", 29, 47),
        // Annotation(Some(3), 1, 1, "human", 76, 81),
        // Annotation(Some(4), 1, 2, "e. coli", 162, 169)
      // )
    // )
  // )

// }

// class LinnaeusNERAdapterSpec extends LinnaeusSpecSetup {

  // "The Linnaeus Annotator" - {

    // "should be able to annotate species in Documents" in new WithApplication {
      // val dal = DAL(DB("test").driver)

      // import dal._
      // import dal.profile.simple._

      // DB("test") withSession { implicit session : Session =>
        // forAll (expectations) { (document, entities, annotations) =>

          // info(s"checking validity of annotations for document '${document.title}'")
          // dal.create

          // val linnaeus = system.actorOf(Props(classOf[LinnaeusNERAdapter], dal))
          // linnaeus ! session

          // Documents += document

          // linnaeus ! AnnotateDocument(document)
          // expectMsg(10.seconds, FinishedAnnotation(document))

          // NamedEntities.list should contain theSameElementsAs (entities)
          // Annotations.list   should contain theSameElementsAs (annotations)

          // linnaeus ! PoisonPill
          // dal.drop

        // }
      // }
    // }

  // }

// }

