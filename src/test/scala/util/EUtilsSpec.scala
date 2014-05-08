package es.uvigo.esei.tfg.smartdrugsearch.util

import org.joda.time.{ DateTimeZone, LocalDate }

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class EUtilsSpec extends BaseSpec {

  "EUTils util object" - {

    "can get the Taxonomy Scientific Name from an NCBI Taxonomy ID" in {
      val human = EUtils() taxonomyScientificName 9606
      val eColi = EUtils() taxonomyScientificName 562

      human       should be ('defined)
      eColi       should be ('defined)
      eColi.value should be ("Escherichia coli")
      human.value should be ("Homo sapiens")
    }

    "can search articles in the PubMed database" in {
      val query = EUtils() searchInPubMed ("breast cancer", None, 0, 0) flatMap (_.QueryTranslation)

      query       should be ('defined)
      query.value should be (
        """"breast neoplasms"[MeSH Terms] OR ("breast"[All Fields] AND "neoplasms"[All Fields]) OR """ +
        """"breast neoplasms"[All Fields] OR ("breast"[All Fields] AND "cancer"[All Fields]) OR """    +
        """"breast cancer"[All Fields]"""
      )
    }

    "can search articles in the PubMed database with Entrez Date within last given days" in {
      val lastDays = 60
      val today    = new LocalDate(DateTimeZone forID "America/New_York")
      val before   = today minusDays lastDays

      val query = EUtils() searchInPubMed ("cancer", Some(lastDays), 0, 0) flatMap (_.QueryTranslation)

      query       should be ('defined)
      query.value should be (
        """("neoplasms"[MeSH Terms] OR "neoplasms"[All Fields] OR "cancer"[All Fields]) AND """ +
        s"""${before toString "yyyy/MM/dd"}[EDAT] : ${today toString "yyyy/MM/dd"}[EDAT]"""
      )
    }

    "can fetch articles from the PubMed database given a list of Pubmed IDs" in {
      val articles = EUtils() fetchPubMedArticles Seq(9997, 17284678)
      articles should have size 2

      articles should contain theSameElementsAs List(
        (
          9997,
          "Magnetic studies of Chromatium flavocytochrome C552. A mechanism for heme-flavin interaction.",
          "Electron paramagnetic resonance and magnetic susceptibility studies "    +
          "of Chromatium flavocytochrome C552 and its diheme flavin-free subunit "  +
          "at temperatures below 45 degrees K are reported. The results show that " +
          "in the intact protein and the subunit the two low-spin (S = 1/2) heme "  +
          "irons are distinguishable, giving rise to separate EPR signals. In the " +
          "intact protein only, one of the heme irons exists in two different low " +
          "spin environments in the pH range 5.5 to 10.5, while the other remains " +
          "in a constant environment. Factors influencing the variable heme iron "  +
          "environment also influence flavin reactivity, indicating the existence " +
          "of a mechanism for heme-flavin interaction."
        ),
        (
          17284678,
          "Sequencing and analysis of chromosome 1 of Eimeria tenella reveals a unique segmental organization.",
          "Eimeria tenella is an intracellular protozoan parasite that infects "    +
          "the intestinal tracts of domestic fowl and causes coccidiosis, a "       +
          "serious and sometimes lethal enteritis. Eimeria falls in the same "      +
          "phylum (Apicomplexa) as several human and animal parasites such as "     +
          "Cryptosporidium, Toxoplasma, and the malaria parasite, Plasmodium. "     +
          "Here we report the sequencing and analysis of the first chromosome of "  +
          "E. tenella, a chromosome believed to carry loci associated with drug "   +
          "resistance and known to differ between virulent and attenuated strains " +
          "of the parasite. The chromosome--which appears to be representative of " +
          "the genome--is gene-dense and rich in simple-sequence repeats, many of " +
          "which appear to give rise to repetitive amino acid tracts in the "       +
          "predicted proteins. Most striking is the segmentation of the "           +
          "chromosome into repeat-rich regions peppered with transposon-like "      +
          "elements and telomere-like repeats, alternating with repeat-free "       +
          "regions. Predicted genes differ in character between the two types of "  +
          "segment, and the repeat-rich regions appear to be associated with "      +
          "strain-to-strain variation."
        )
      )
    }

  }

}

