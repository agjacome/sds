package es.uvigo.ei.sing.sds.service

import org.joda.time.{ DateTimeZone, LocalDate }

import es.uvigo.ei.sing.sds.BaseSpec
import es.uvigo.ei.sing.sds.entity._

class EUtilsServiceSpec extends BaseSpec {

  // TODO: mock the web service calls, and test that findPubMedIds returns a
  // valid set of IDs and Size (cannot correctly test without mocking, since a
  // search result can vary as the PubMed database); and with that done, the
  // "searchInPubMed" can be made private and that ugly equality tests on the
  // QueryTranslation can be removed

  private[this] lazy val taxonomyNames = Table(
    ("id"  , "name"            ),
    (562L  , "Escherichia coli"),
    (7091L , "Bombyx mori"     ),
    (9606L , "Homo sapiens"    )
  )

  private[this] lazy val articleTitles = Table(
    ("ids", "titles"),
    (
      Set[PubMedId](9997, 17284678, 24820078),
      Set[Sentence](
        "Magnetic studies of Chromatium flavocytochrome C552. A mechanism for heme-flavin interaction.",
        "Sequencing and analysis of chromosome 1 of Eimeria tenella reveals a unique segmental organization.",
        "Epinecidin-1 has immunomodulatory effects, facilitating its therapeutic use in a mouse model of Pseudomonas aeruginosa sepsis."
      )
    )
  )

  private[this] lazy val eUtils = EUtilsService()

  "The EUtils Service" - {

    "can get the Taxonomy Scientific Name from a NCBI Taxonomy ID" in {
      forAll(taxonomyNames) { (id : Long, name : String) =>
        val res = eUtils taxonomyScientificName id
        res should be ('defined)
        res.value.toLowerCase should be (name.toLowerCase)
      }
    }

    "can search articles in the PubMed database" in {
      val query = eUtils searchInPubMed ("breast cancer", None, 0, 0) flatMap (_.QueryTranslation)
      query should be ('defined)
      query.value should be (
        """"breast neoplasms"[MeSH Terms] OR ("breast"[All Fields] AND "neoplasms"[All Fields]) OR """ +
        """"breast neoplasms"[All Fields] OR ("breast"[All Fields] AND "cancer"[All Fields]) OR """    +
        """"breast cancer"[All Fields]"""
      )
    }

    "can search articles in the PubMed database where their Entrez Date is within some last given day range" in {
      val today = new LocalDate(DateTimeZone forID "America/New_York")
      val limit = today minusDays 60
      val query = eUtils searchInPubMed ("cancer", Some(60), 0, 0) flatMap (_.QueryTranslation)

      query       should be ('defined)
      query.value should be (
        """("neoplasms"[MeSH Terms] OR "neoplasms"[All Fields] OR "cancer"[All Fields]) AND """ +
        s"""${limit toString "yyyy/MM/dd"}[EDAT] : ${today toString "yyyy/MM/dd"}[EDAT]"""
      )
    }

    "can fetch articles from the PubMed database given a list of PubMed IDs" in {
      forAll(articleTitles) { (ids : Set[PubMedId], titles : Set[Sentence]) =>
        eUtils fetchPubMedArticles ids map (_.title) should contain theSameElementsAs titles
      }
    }

  }

}

