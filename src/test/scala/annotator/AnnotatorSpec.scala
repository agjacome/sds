package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.duration._
import akka.actor.{ PoisonPill, Props }

import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.{ ABNERUtils, LinnaeusUtils, OscarUtils }

private[annotator] trait AnnotatorSpecSetup extends AnnotatorBaseSpec {

  protected def cleanSpaces(str : String) : String =
    str.stripMargin filter (_ >= ' ')

  protected val expectations = Table(
    ("document", "keywords", "annotations"),
    (
      Document(
        Some(1), cleanSpaces(
          """|Additive effects of orexin B and vasoactive intestinal polypeptide
             |on LL-37-mediated antimicrobial activities"""
        ), cleanSpaces(
          """|The present study examined the bactericidal effects of orexin B
             |(ORXB) and vasoactive intestinal peptide (VIP) alone or combined
             |with cationic antimicrobial peptides, such as LL-37, on
             |Escherichia coli, Pseudomonas aeruginosa, Streptococcus mutans
             |and Staphylococcus aureus. The bactericidal effect of ORXB or VIP
             |alone was detected in low NaCl concentration, but attenuated in
             |physiological NaCl concentration (150 mM). However, such
             |attenuated bactericidal activities of ORXB and VIP in 150 mM NaCl
             |were regained by adding LL-37. Therefore, our results indicate
             |that VIP and ORXB appear to mediate bactericidal effects in
             |concert with LL-37 in the physiological context of mucosal tissue."""
        ), false, Some(21176972)
      ),
      Seq(
        Keyword(Some(1), "orexin B",                                       Protein,  1),
        Keyword(Some(2), "LL-37",                                          Protein,  2),
        Keyword(Some(3), "ORXB or VIPalone",                               DNA,      1),
        Keyword(Some(4), "ORXB",                                           Protein,  2),
        Keyword(Some(5), "Pseudomonas aeruginosa",                         Species,  1),
        Keyword(Some(6), "Staphylococcus aureus",                          Species,  1),
        Keyword(Some(7), "InChI=1/ClH.Na/h1H;/q;+1/p-1/fCl.Na/h1h;/q-1;m", Compound, 2)
      ),
      Seq(
        Annotation(Some( 1), 1, 1, "orexin B",                55,  63),
        Annotation(Some( 2), 1, 2, "LL-37",                  173, 178),
        Annotation(Some( 3), 1, 3, "ORXB or VIPalone",       298, 314),
        Annotation(Some( 4), 1, 4, "ORXB",                   466, 470),
        Annotation(Some( 5), 1, 4, "ORXB",                   568, 572),
        Annotation(Some( 6), 1, 2, "LL-37",                  627, 632),
        Annotation(Some( 7), 1, 5, "Pseudomonas aeruginosa", 200, 222),
        Annotation(Some( 8), 1, 6, "Staphylococcus aureus",  248, 269),
        Annotation(Some( 9), 1, 7, "NaCl",                   335, 339),
        Annotation(Some(10), 1, 7, "NaCl",                   386, 390)
      )
    )
  )

}

class AnnotatorSpec extends AnnotatorSpecSetup {

  import dbProfile.{ Annotations, Documents, Keywords }
  import dbProfile.profile.simple._

  // Force ABNER, OSCAR and LINNAEUS load (because they are lazy)
  ABNERUtils.abner
  LinnaeusUtils.linnaeus
  OscarUtils.oscar.findResolvableEntities("")
  OscarUtils.normalizer.parseToInchi("")

  "The global Annotator" - {

    "should be able to annotate different kinds of keywords in Documents" - {

      forAll (expectations) { (document, keywords, annotations) =>
        s"checking validity of annotations for Document '${document.title}'" in new WithApplication {

          Documents += document
          val annotator = system.actorOf(Props[Annotator], "Annotator")

          annotator ! document
          expectNoMsg(5.seconds)
          annotator ! PoisonPill

          val expectedKeywords  = keywords      map { k => (k.normalized, k.category, k.occurrences) }
          val actualKeywords    = Keywords.list map { k => (k.normalized, k.category, k.occurrences) }
          expectedKeywords should contain theSameElementsAs actualKeywords

          val expectedAnnotations = annotations      map { a => (a.text, a.startPos, a.endPos) }
          val actualAnnotations   = Annotations.list map { a => (a.text, a.startPos, a.endPos) }
          expectedAnnotations should contain theSameElementsAs actualAnnotations

          (Documents map (_.annotated)).first should be (true)
        }
      }

    }

    "should not annotate already annotated Documents" in new WithApplication {
      Documents += Document(title = "annotated document", text = "an already annotated document", annotated = true)
      val document = Documents.first

      val annotator = system.actorOf(Props[Annotator], "Annotator")

      annotator ! document
      expectNoMsg()
      annotator ! PoisonPill

      Keywords.list    should be ('empty)
      Annotations.list should be ('empty)
    }

  }

}

