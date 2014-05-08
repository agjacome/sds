package es.uvigo.esei.tfg.smartdrugsearch.annotator

import akka.actor.{ PoisonPill, Props }

import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.util.ABNERUtils

private[annotator] trait ABNERSpecSetup extends AnnotatorBaseSpec {

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
      Seq(
        Keyword(Some( 1), "transcriptional repressor", Protein, 1),
        Keyword(Some( 2), "Nrg1", Protein, 1),
        Keyword(Some( 3), "reveal negative factors", Protein, 1),
        Keyword(Some( 4), "STA1", Protein, 1),
        Keyword(Some( 5), "glucoamylase", Protein, 1),
        Keyword(Some( 6), "NRG1 gene", DNA, 2),
        Keyword(Some( 7), "C2H2 zinc finger protein", Protein, 1),
        Keyword(Some( 8), "upstream activation sequence", DNA, 1),
        Keyword(Some( 9), "STA1 gene", DNA, 1),
        Keyword(Some(10), "DNase I", Protein, 1),
        Keyword(Some(11), "STA1 transcript", RNA, 1)
      ),
      Seq(
        Annotation(Some( 1), 1,  1, "transcriptional repressor", 21, 46),
        Annotation(Some( 2), 1,  2, "Nrg1", 48, 52),
        Annotation(Some( 3), 1,  3, "reveal negative factors", 86, 109),
        Annotation(Some( 4), 1,  4, "STA1", 140, 144),
        Annotation(Some( 5), 1,  5, "glucoamylase", 162, 174),
        Annotation(Some( 6), 1,  6, "NRG1 gene", 180, 189),
        Annotation(Some( 7), 1,  7, "C2H2 zinc finger protein", 207, 231),
        Annotation(Some( 8), 1,  8, "upstream activation sequence", 279, 307),
        Annotation(Some( 9), 1,  9, "STA1 gene", 316, 325),
        Annotation(Some(10), 1, 10, "DNase I", 356, 363),
        Annotation(Some(11), 1,  6, "NRG1 gene", 402, 411),
        Annotation(Some(12), 1, 11, "STA1 transcript", 459, 474)
      )
    )
  )

}

class ABNERAdapterSpec extends ABNERSpecSetup {

  import dbProfile.{ Annotations, Documents, Keywords }
  import dbProfile.profile.simple._

  // Force ABNER load (because it is lazy)
  ABNERUtils.abner

  "The ABNER Annotator" - {

    "should be able to annotate proteins in a Document" - {

      forAll (expectations) { (document, keywords, annotations) =>
        s"checking validity of annotations for Document '${document.title}'" in new WithApplication {
          Documents += document
          val abner = system.actorOf(Props[ABNERAdapter])

          abner ! Annotate(document)
          expectMsg(Finished(document))
          abner ! PoisonPill

          Keywords.list    should contain theSameElementsAs keywords
          Annotations.list should contain theSameElementsAs annotations
        }
      }

    }

  }

}

