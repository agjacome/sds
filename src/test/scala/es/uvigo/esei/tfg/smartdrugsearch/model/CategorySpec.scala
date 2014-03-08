package es.uvigo.esei.tfg.smartdrugsearch.model

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import Category._

class CategorySpec extends BaseSpec {

  "A Category" - {

    "defines values for" - {
      "compounds" in { val value = Category.Compound }
      "drugs"     in { val value = Category.Drug     }
      "genes"     in { val value = Category.Gene     }
      "proteins"  in { val value = Category.Protein  }
      "species"   in { val value = Category.Species  }
    }

    "can be constructed" - {
      "implicitly from a String" in {
        val drug : Category = "drug"
        drug should be (Drug)

        val species : Category = "species"
        species should be (Species)
      }
      "explicitly from a String with the 'withName' method" in {
        val drug = Category withName "drug"
        drug should be (Drug)

        val species = Category withName "species"
        species should be (Species)
      }
    }

    "can be converted to a String" - {
      "implicitly" in {
        val drug : String = Drug
        drug should be ("drug")

        val species : String = Species
        species should be ("species")
      }
      "explicitly with the 'toString' method" in {
        val drug = Drug.toString
        drug should be ("drug")

        val species = Species.toString
        species should be ("species")
      }
    }

    "should throw a NoSuchElementException" - {
      "when implicitly constructed from an invalid String" in {
        a [NoSuchElementException] should be thrownBy {
          val invalid : Category = "THIS_IS_AN_INVALID_CATEGORY_STRING"
        }
      }
      "when explicitly constructed from an invalid String" in {
        a [NoSuchElementException] should be thrownBy {
          val invalid : Category = Category withName "THIS_IS_ANOTHER_INVALID_CATEGORY_STRING"
        }
      }
    }

  }

}

