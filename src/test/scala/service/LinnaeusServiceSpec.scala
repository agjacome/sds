package es.uvigo.esei.tfg.smartdrugsearch.service

import scala.concurrent.duration._
import scala.language.postfixOps

import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.WithApplication

import uk.ac.man.entitytagger.Mention

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.entity._

class LinnaeusServiceSpec extends BaseSpec {

  private[this] lazy val docMentions = Table(
    ("text", "mentions"),
    (
      "We have boy a hub identified IGF022/01 cellline Buchnera aphidicola a novel human cDNA with a predicted protein sequence that has 28% amin acid identity with the E. coli Hsp70 co-chaperone GrpE and designated it HMGE",
      Seq(
        new Mention("species:ncbi:9606",   8,  11, "boy"),
        new Mention("species:ncbi:9606",  29,  47, "IGF022/01 cellline"),
        new Mention("species:ncbi:9606",  76,  81, "human"),
        new Mention("species:ncbi:562",  162, 169, "E. coli")
      )
    )
  )

  private[this] lazy val mentionNormalized = Table(
    ("mention", "normalized"),
    (new Mention("species:ncbi:9606", 0, 0, ""), Sentence("Homo sapiens")    ),
    (new Mention("species:ncbi:562",  0, 0, ""), Sentence("Escherichia coli")),
    (new Mention("species:ncbi:7091", 0, 0, ""), Sentence("Bombyx mori")     )
  )

  private[this] lazy val linnaeus = LinnaeusService()

  "The Linnaeus Service" - {

    "should be able to obtain all Species in a text String" in new WithApplication {
      forAll(docMentions) { (text : String, mentions : Seq[Mention]) =>
        whenReady(linnaeus obtainMentions text) { obtained =>
          val expected = mentions map { m => (m.getMostProbableID, m.getStart, m.getEnd, m.getText) }
          val actual   = obtained map { m => (m.getMostProbableID, m.getStart, m.getEnd, m.getText) }
          actual should contain theSameElementsAs expected
        }
      }
    }

    "should be able to normalize a Mention into a species scientific name" in new WithApplication {
      // set cache, this way a web-service call to NCBI's Taxonomy database will not
      // be made, and the scientific names will be recovered from cache (kind-of
      // stubbing out the EUtils Service, without really stubbing anything)
      Cache.set("Taxonomy(562)",  "Escherichia coli", 5 seconds)
      Cache.set("Taxonomy(7091)", "Bombyx mori",      5 seconds)
      Cache.set("Taxonomy(9606)", "Homo sapiens",     5 seconds)

      forAll(mentionNormalized) { (mention : Mention, normalized : Sentence) =>
        linnaeus normalize mention should equal (normalized)
      }
    }

  }

}
