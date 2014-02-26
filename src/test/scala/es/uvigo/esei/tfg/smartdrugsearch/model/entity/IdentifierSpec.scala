package es.uvigo.esei.tfg.smartdrugsearch.model.entity

import org.scalatest._

class IdentifierSpec extends FlatSpec with Matchers {

  "An Identifier" should "be just a non-negative Integer value" in {
    Identifier(0).id   should be (0)
    Identifier(1).id   should be (1)
    Identifier(10).id  should be (10)
    Identifier(500).id should be (500)
  }

  it must "throw an IllegalArgumentException if a negative Integer value is given" in {
    a [IllegalArgumentException] should be thrownBy { Identifier(-1)   }
    a [IllegalArgumentException] should be thrownBy { Identifier(-5)   }
    a [IllegalArgumentException] should be thrownBy { Identifier(-10)  }
    a [IllegalArgumentException] should be thrownBy { Identifier(-100) }
  }

  it can "be implicitly created from an Integer, given that Identifier.Predef is imported" in {
    import Identifier.Predef._

    val id1 : Identifier = 1
    val id2 : Identifier = 2
    val id3 : Identifier = 500
  }

}

