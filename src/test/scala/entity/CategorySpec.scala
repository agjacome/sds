package es.uvigo.esei.tfg.smartdrugsearch.entity

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class CategorySpec extends BaseSpec {

  "A Category" - {

    "defines values for" - {
      "compounds"  in { Compound }
      "drugs"      in { Drug     }
      "genes"      in { Gene     }
      "proteins"   in { Protein  }
      "species"    in { Species  }
      "dna"        in { DNA      }
      "rna"        in { RNA      }
      "cell lines" in { CellLine }
      "cell types" in { CellType }
    }

    "can be constructed" - {
      "with an Integer representing a Category ID" in {
        val drug = Category(2)
        drug should be (Drug)

        val species = Category(5)
        species should be (Species)
      }
      "with an String representing a Category" in {
        val drug = Category("drug")
        drug should be (Drug)

        val species = Category("species")
        species should be (Species)
      }
    }

    "can be converted to a String" in {
      val drug = Drug.toString
      drug should be ("Drug")

      val species = Species.toString
      species should be ("Species")
    }

    "should throw a NoSuchElementException" - {
      "when constructed from an invalid Integer ID" in {
        a [NoSuchElementException] should be thrownBy {
          val invalid = Category(123)
        }
      }
      "when constructed from an invalid String" in {
        a [NoSuchElementException] should be thrownBy {
          val invalid = Category("THIS_IS_AN_INVALID_CATEGORY_STRING")
        }
      }
    }

  }

}

