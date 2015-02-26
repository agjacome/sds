package es.uvigo.ei.sing.sds.entity

import play.api.libs.json._
import org.scalacheck.Gen

import es.uvigo.ei.sing.sds.BaseSpec

class SizeSpec extends BaseSpec {

  private[this] lazy val validSizes = Gen.choose(0, Long.MaxValue / 2) map Size

  "A Size" - {

    "can be constructed" - {

      "implicitly from a 'Long'" in {
        forAll((n : Long) => whenever(n >= 0) {
          val size : Size = n
          size should be (Size(n))
        })
      }

      "explicitly by passing a 'Long' value to its constuctor" in {
        forAll((n : Long) => whenever(n >= 0) {
          val size = Size(n)
          size.value should be (n)
        })
      }

      "by parsing a JSON Number" in {
        forAll((n : Long) => whenever(n >= 0) {
          val size = JsNumber(n).as[Size]
          size should be (Size(n))
        })
      }

    }

    "can be compared with expected behaviour" - {

      "with another Size" in {
        forAll(validSizes, validSizes) { (x : Size, y : Size) =>
          if      (x.value < y.value) x should be < y
          else if (x.value > y.value) x should be > y
          else                        x should equal (y)
        }
      }

    }

    "can perform operations with another Size" - {

      "addition" in {
        forAll(validSizes, validSizes) { (x : Size, y : Size) =>
          (x + y) should be (Size(x.value + y.value))
        }
      }

      "subtraction" in {
        forAll(validSizes, validSizes) { (x : Size, y : Size) =>
          if (x.value > y.value) (x - y) should be (Size(x.value - y.value))
          else                   (y - x) should be (Size(y.value - x.value))
        }
      }

    }

    "can be converted to" - {

      "a 'Long' value" - {
        "implicitly" in {
          forAll((n : Long) => whenever(n >= 0) {
            val num : Long = Size(n)
            num should be (n)
          })
        }
        "explicitly" in {
          forAll((n : Long) => whenever(n >= 0) {
            val num = Size(n).value
            num should be (n)
          })
        }
      }

      "a JSON Number" in {
        forAll((n : Long) => whenever(n >= 0) {
          val num = Json toJson Size(n)
          num should be (JsNumber(n))
        })
      }

    }

    "should throw a IllegalArgumentException" - {

      "when implicitly constructed from a negative Long" in {
        forAll((n : Long) => whenever(n < 0) {
          a [IllegalArgumentException] should be thrownBy {
            val size : Long = Size(n)
          }
        })
      }

      "when explicitly constructed with a negative Long as parameter" in {
        forAll((n : Long) => whenever(n < 0) {
          a [IllegalArgumentException] should be thrownBy {
            val size = Size(n)
          }
        })
      }

    }

  }

}

