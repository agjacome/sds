package es.uvigo.ei.sing.sds.entity

import play.api.libs.json._
import org.scalacheck.Arbitrary.arbitrary

import es.uvigo.ei.sing.sds.BaseSpec

// first level because value classes cannot be members of another classes
private case class TestId(value : Long) extends AnyVal with Identifier
private object TestId extends IdentifierCompanion[TestId]

class IdentifierSpec extends BaseSpec {

  private[this] lazy val validTestIds = arbitrary[Long] map TestId

  "An Identifier" - {

    "can be constructed" - {

      "with an anonymous object that extends Identifier" in {
        forAll { (n : Long) =>
          val id = new Identifier { val value : Long = n }
          id.value should be (n)
        }
      }

      "with a class that extends/mixes-in Identifier" in {
        forAll { (n : Long) =>
          val id = TestId(n)
          id.value should be (n)
        }
      }

      "implicitly from a Long whenever an IdentifierCompanion is in scope" in {
        forAll { (n : Long) =>
          val id : TestId = n
          id.value should be (n)
        }
      }

      "by parsing a JSON Number" in {
        forAll { (n : Long) =>
          val id = JsNumber(n).as[TestId]
          id.value should be (n)
        }
      }

    }

    "can be compared in expected behaviour" - {
      "with another Identifier of the same class" in {
        forAll(validTestIds, validTestIds) { (x : TestId, y : TestId) =>
          if      (x.value < y.value) x should be < y
          else if (x.value > y.value) x should be > y
          else                        x should equal (y)
        }
      }
    }

    "can be converted to" - {

      "a Long value"  - {
        "implicitly" in {
          forAll{ (n : Long) =>
            val num : Long = TestId(n)
            num should be (n)
          }
        }
        "explicitly" in {
          forAll{ (n : Long) =>
            val num : Long = TestId(n).value
            num should be (n)
          }
        }
      }

      "a JSON Number" in {
        forAll{ (n : Long) =>
          val num = Json toJson TestId(n)
          num should be (JsNumber(n))
        }
      }

    }

  }

}

