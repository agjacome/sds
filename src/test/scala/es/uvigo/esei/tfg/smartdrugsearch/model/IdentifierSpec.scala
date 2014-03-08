package es.uvigo.esei.tfg.smartdrugsearch.model

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class IdentifierSpec extends BaseSpec {

  private case class TestId(value : Long) extends Identifier
  private object TestId extends IdentifierCompanion[TestId]

  "An Identifier" - {

    "can be constructed" - {
      "with an anonymous object that extends Identifier" in {
        val idOne = new Identifier { val value : Long = 10 }
        idOne.value should be (10)

        val idTwo = new Identifier { val value : Long = -10 }
        idTwo.value should be (-10)
      }
      "with a class that extends/mixes-in Identifier" in {
        val idOne = TestId(10)
        idOne.value should be (10)

        val idTwo = TestId(-10)
        idTwo.value should be (-10)
      }
      "implicitly from a Long whenever an IdentifierCompanion is in scope" in {
        val idOne : TestId = 10
        idOne.value should be (10)

        val idTwo : TestId = -10
        idTwo.value should be (-10)
      }
    }

    "can be compared in expected behaviour" - {
      "with another Identifier of the same class" in {
        val idOne   = TestId(1)
        val idTwo   = TestId(10)
        val idThree = TestId(-1)

        idOne should be < (idTwo)
        idOne should be > (idThree)
        idTwo should be > (idThree)
      }
    }

    "can be converted to a Long value" - {
      "implicitly" in {
        val one : Long = TestId(1)
        one should be (1)

        val ten : Long = TestId(10)
        ten should be (10)
      }
      "explicitly with the 'value' attribute" in {
        val one : Long = TestId(1).value
        one should be (1)

        val ten : Long = TestId(10).value
        ten should be (10)
      }
    }

  }

}

