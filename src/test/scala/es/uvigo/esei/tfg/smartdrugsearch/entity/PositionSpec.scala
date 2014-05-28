package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import org.scalacheck.Gen

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class PositionSpec extends BaseSpec {

  private[this] lazy val validPositions = Gen.choose(0, Long.MaxValue / 2) map Position

  "A Position" - {

    "can be constructed" - {

      "implicitly from a 'Long'" in {
        forAll((n : Long) => whenever(n >= 0) {
          val pos : Position = n
          pos should be (Position(n))
        })
      }

      "explicitly by passing a 'Long' value to its constuctor" in {
        forAll((n : Long) => whenever(n >= 0) {
          val pos = Position(n)
          pos.value should be (n)
        })
      }

      "by parsing a JSON Number" in {
        forAll((n : Long) => whenever(n >= 0) {
          val pos = JsNumber(n).as[Position]
          pos should be (Position(n))
        })
      }

    }

    "can be compared with expected behaviour" - {

      "with another Position" in {
        forAll(validPositions, validPositions) { (x : Position, y : Position) =>
          if      (x.value < y.value) x should be < y
          else if (x.value > y.value) x should be > y
          else                        x should equal (y)
        }
      }

    }

    "can perform operations with another Position" - {

      "addition" in {
        forAll(validPositions, validPositions) { (x : Position, y : Position) =>
          (x + y) should be (Position(x.value + y.value))
        }
      }

      "subtraction" in {
        forAll(validPositions, validPositions) { (x : Position, y : Position) =>
          if (x.value > y.value) (x - y) should be (Position(x.value - y.value))
          else                   (y - x) should be (Position(y.value - x.value))
        }
      }

    }

    "can be converted to" - {

      "a 'Long' value" - {
        "implicitly" in {
          forAll((n : Long) => whenever(n >= 0) {
            val num : Long = Position(n)
            num should be (n)
          })
        }
        "explicitly" in {
          forAll((n : Long) => whenever(n >= 0) {
            val num = Position(n).value
            num should be (n)
          })
        }
      }

      "a JSON Number" in {
        forAll((n : Long) => whenever(n >= 0) {
          val num = Json toJson Position(n)
          num should be (JsNumber(n))
        })
      }

    }

    "should throw a IllegalArgumentException" - {

      "when implicitly constructed from a negative Long" in {
        forAll((n : Long) => whenever(n < 0) {
          a [IllegalArgumentException] should be thrownBy {
            val position : Long = Position(n)
          }
        })
      }

      "when explicitly constructed with a negative Long as parameter" in {
        forAll((n : Long) => whenever(n < 0) {
          a [IllegalArgumentException] should be thrownBy {
            val position = Position(n)
          }
        })
      }

    }

  }

}

