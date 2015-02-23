package es.uvigo.esei.tfg.smartdrugsearch.service

import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity._

class ABNERServiceSpec extends BaseSpec {

  private[this] lazy val docEntities = Table(
    ("text", "entities"),
    (
      "We have identified a transcriptional repressor, Nrg1, in a genetic screen designed to reveal negative factors involved in the expression of STA1, which encodes a glucoamylase. The NRG1 gene encodes a 25-kDa C2H2 zinc finger protein which specifically binds to two regions in the upstream activation sequence of the  STA1 gene, as judged by gel retardation DNase I footprint analyses. Disruption of the NRG1 gene causes a fivefold increase in the level of the STA1 transcript in the presence of glucose.",
      Seq(
        ABNEREntity("transcriptional repressor",    Protein,  21,  46),
        ABNEREntity("Nrg1",                         Protein,  48,  52),
        ABNEREntity("reveal negative factors",      Protein,  86, 109),
        ABNEREntity("STA1",                         Protein, 140, 144),
        ABNEREntity("glucoamylase",                 Protein, 162, 174),
        ABNEREntity("NRG1 gene",                    DNA,     180, 189),
        ABNEREntity("C2H2 zinc finger protein",     Protein, 207, 231),
        ABNEREntity("upstream activation sequence", DNA,     279, 307),
        ABNEREntity("STA1 gene",                    DNA,     316, 325),
        ABNEREntity("DNase I",                      Protein, 356, 363),
        ABNEREntity("NRG1 gene",                    DNA,     402, 411),
        ABNEREntity("STA1 transcript",              RNA,     459, 474)
      )
    )
  )

  private[this] lazy val entityNormalized = Table(
    ("entity", "normalized"),
    (ABNEREntity("glucoamylase",    Protein, 162, 174), Sentence("glucoamylase")   ),
    (ABNEREntity("NRG1 gene",       DNA,     180, 189), Sentence("nrg1 gene")      ),
    (ABNEREntity("DNase I",         Protein, 356, 363), Sentence("dnase i")        ),
    (ABNEREntity("STA1 transcript", RNA,     459, 474), Sentence("sta1 transcript"))
  )

  private[this] lazy val abner = ABNERService()

  "The ABNER Service" - {

    "should be able to obtain Proteins in a text String" in new WithApplication {
      forAll(docEntities) { (text : String, entities : Seq[ABNEREntity]) =>
        whenReady(abner getEntities text) {
          _ should contain theSameElementsAs entities
        }
      }
    }

    "should perform normalization of ABNER entities by just lowercasing its text" in new WithApplication {
      forAll(entityNormalized) { (entity : ABNEREntity, normalized : Sentence) =>
        abner normalize entity should equal (normalized)
      }
    }

  }

}


