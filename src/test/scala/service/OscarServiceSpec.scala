package es.uvigo.esei.tfg.smartdrugsearch.service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import play.api.test.WithApplication
import org.mockito.Mockito.when

import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities._

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity._

class OscarServiceSpec extends BaseSpec {

  private[this] lazy val docEntities = Table(
    ("text", "entities"),
    ("Then we mix benzene with napthyridine and toluene.", Seq(("benzene", 12, 19), ("toluene", 42, 49)))
  )

  private[this] lazy val entityNormalized = Table(
    ("entity", "normalized"),
    (mock[ResolvedNamedEntity], Sentence("InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H")      ),
    (mock[ResolvedNamedEntity], Sentence("InChI=1/C7H8/c1-7-5-3-2-4-6-7/h2-6H,1H3"))
  )

  private[this] lazy val oscar = OscarService()

  "The Oscar Service" - {

    "should be able to obtain al NamedEntities in a text String" in new WithApplication {
      forAll(docEntities) { (text : String, entities : Seq[(String, Int, Int)]) =>
        whenReady(oscar getNamedEntities text) { obtained =>
          val annotated = obtained map { e => (e.getSurface, e.getStart, e.getEnd) }
          annotated should contain theSameElementsAs entities
        }
      }
    }

    "should be able to normalize a NamedEntity into its INCHI representation" in new WithApplication {
      forAll(entityNormalized) { (entity : ResolvedNamedEntity, normalized : Sentence) =>
        when(entity getFirstChemicalStructure FormatType.INCHI) thenReturn new ChemicalStructure(
          normalized.toString, FormatType.INCHI, null
        )
        oscar normalize entity should equal (normalized)
      }
    }

  }


}
