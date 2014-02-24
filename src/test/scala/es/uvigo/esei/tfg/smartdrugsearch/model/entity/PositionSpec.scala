package es.uvigo.esei.tfg.smartdrugsearch.model.entity

import org.scalatest._

class PositionSpec extends FlatSpec with Matchers {

  "A Position" should "be just a positive Integer" in {
    Position(10)
    Position(0)
    Position(190842)
  }

  it should "throw an IllegalArgumentException if a negative Integer is given" in {
    a [IllegalArgumentException] should be thrownBy { Position(-1)       }
    a [IllegalArgumentException] should be thrownBy { Position(-10)      }
    a [IllegalArgumentException] should be thrownBy { Position(-1129843) }
  }

  it can "be compared to another Position with expected behaviour" in {
    Position(0)        should be <  Position(1)
    Position(1)        should be >  Position(0)
    Position(12302343) should be >= Position(3)
    Position(190)      should be <= Position(200)
  }

  it can "be implicitly created from a positive integer given Position.Predef is imported" in {
    import Position.Predef._

    val pos1 : Position = 0
    val pos2 : Position = 1
    val pos3 : Position = 129472
  }


}
