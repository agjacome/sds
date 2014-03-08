package es.uvigo.esei.tfg.smartdrugsearch.model

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import Category._

class NamedEntitySpec extends BaseSpec {

  "A Named Entity" - {

    "can be constructed" - {
      "by using an Optional NamedEntityId, a Sentence as normalized text, a Category and number of occurrences" in {
        val entOne = NamedEntity(None, Sentence.Empty, Drug) // occurrences is optional param. (default to 0)
        entOne should have (
          'id          (None),
          'normalized  (Sentence.Empty),
          'category    (Drug),
          'occurrences (0)
        )

        val entTwo = NamedEntity(Some(3), "escherichia coli", Species, 13)
        entTwo should have (
          'id          (Some(NamedEntityId(3))),
          'normalized  (Sentence("escherichia coli")),
          'category    (Species),
          'occurrences (13)
        )
      }
    }

    "should throw an IllegalArgumentException" - {
      "when constructed with a negative number of occurrences" in {
        a [IllegalArgumentException] should be thrownBy { NamedEntity(None,     Sentence.Empty, Protein, -1)  }
        a [IllegalArgumentException] should be thrownBy { NamedEntity(Some(10), "homo sapiens", Species, -5)  }
        a [IllegalArgumentException] should be thrownBy { NamedEntity(Some(23), "ceftazidime",  Drug,    -10) }
      }
    }

  }

}

