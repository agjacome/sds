package es.uvigo.esei.tfg.smartdrugsearch.entity

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class KeywordSpec extends BaseSpec {

  "A Keyword" - {

    "can be constructed" - {
      "by using an Optional KeywordId, a Sentence as normalized text, a Category and number of occurrences" in {
        val keyOne = Keyword(normalized = Sentence.Empty, category = Drug)
        keyOne should have (
          'id          (None),
          'normalized  (Sentence.Empty),
          'category    (Drug),
          'occurrences (0)
        )

        val keyTwo = Keyword(Some(3), "escherichia coli", Species, 13)
        keyTwo should have (
          'id          (Some(KeywordId(3))),
          'normalized  (Sentence("escherichia coli")),
          'category    (Species),
          'occurrences (13)
        )
      }
    }

    "should throw an IllegalArgumentException" - {
      "when constructed with a negative number of occurrences" in {
        a [IllegalArgumentException] should be thrownBy { Keyword(None,     Sentence.Empty, Protein, -1)  }
        a [IllegalArgumentException] should be thrownBy { Keyword(Some(10), "homo sapiens", Species, -5)  }
        a [IllegalArgumentException] should be thrownBy { Keyword(Some(23), "ceftazidime",  Drug,    -10) }
      }
    }

  }

}

