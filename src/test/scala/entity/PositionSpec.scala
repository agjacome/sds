package es.uvigo.esei.tfg.smartdrugsearch.entity

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class PositionSpec extends BaseSpec {

  "A Position" - {

    "can be constructed" - {
      "implicitly from a 'Long'" in {
        val posOne : Position = 1
        posOne.value should be (1)

        val posTwo : Position = 10
        posTwo.value should be (10)
      }
      "explicitly by passing a 'Long' value to its constuctor" in {
        val posOne = Position(1)
        posOne.value should be (1)

        val posTwo = Position(10)
        posTwo.value should be (10)
      }
    }

    "can be compared with expected behaviour" - {
      "with another Position" in {
        val posOne   = Position(0)
        val posTwo   = Position(1)
        val posThree = Position(10)

        posOne should be <  posTwo
        posTwo should be <  posThree
        posOne should be <  posThree
        posTwo should be >= posOne
        posOne should be <= posTwo
      }
    }

    "can be converted to a Long" - {
      "implicitly" in {
        val one : Long = Position(1)
        one should be (1)

        val ten : Long = Position(10)
        ten should be (10)
      }
      "explicitly with the 'value' attribute" in {
        val one = Position(1).value
        one should be (1)

        val ten = Position(10).value
        ten should be (10)
      }
    }

    "should throw a IllegalArgumentException" - {
      "when implicitly constructed from a negative Long" in {
        a [IllegalArgumentException] should be thrownBy {
          val invalid : Position = -1
        }
        a [IllegalArgumentException] should be thrownBy {
          val invalid : Position = -10
        }
      }
      "when explicitly constructed with a negative Long as parameter" in {
        a [IllegalArgumentException] should be thrownBy {
          val invalid = Position(-3)
        }
        a [IllegalArgumentException] should be thrownBy {
          val invalid = Position(-17)
        }
      }
    }

  }

}

