package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.Inspectors

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class SentenceSpec extends BaseSpec {

  "A Sentence" - {

    "can be constructed" - {

      "implicitly from a String" in {
        forAll(nonEmptyStringGenerator) { str : String =>
          val s : Sentence = str
          s.words should be (str.trim split "\\s+")
        }
      }

      "explicitly by passing a 'String' to its constructor" in {
        forAll(nonEmptyStringGenerator) { str : String =>
          Sentence(str).words should be (str.trim split "\\s+")
        }
      }

      "as an empty Sentence by using its Empty object" in {
        val emptySentence = Sentence.Empty
        emptySentence.words should be ('empty)
      }

    }

    "can be converted to a 'String'" - {

      "implicitly" in {
        forAll(nonEmptyStringGenerator) { str : String =>
          val s : String = Sentence(str)
          s should equal (str.trim split "\\s+" mkString " ")
        }
      }

      "explicitly with the 'toString' method" in {
        forAll(nonEmptyStringGenerator) { str : String =>
          Sentence(str).toString should equal (str.trim split "\\s+" mkString " ")
        }
      }

      "explicitly with the 'mkString' method using a separator between words" in {
        forAll(nonEmptyStringGenerator, arbitrary[String]) { (str : String, sep : String) =>
          (Sentence(str) mkString sep) should equal (str.trim split "\\s+" mkString sep)
        }
      }

    }

    "should throw an IllegalArgumentException" - {

      "when implicitly constructed from an empty String" in {
        a [IllegalArgumentException] should be thrownBy { val sentence : Sentence = "" }
      }

      "when explicitly constructed from an empty String" in {
        a [IllegalArgumentException] should be thrownBy { val sentence = Sentence("") }
      }

      "when implicitly constructed from a String with just space characters" in {
        a [IllegalArgumentException] should be thrownBy { val sentence : Sentence = "    " }
        a [IllegalArgumentException] should be thrownBy { val sentence : Sentence = "\t\n" }
      }

      "when explicitly constructed from a String with just space characters" in {
        a [IllegalArgumentException] should be thrownBy { val sentence = Sentence("     ") }
        a [IllegalArgumentException] should be thrownBy { val sentence = Sentence("\t\n ") }
      }

    }

    "should transform the String that creates it" - {

      "removing all spaces that it contains" in {
        forAll(nonEmptyStringGenerator) { str : String =>
          all(Sentence(str).words) should not include regex("\\s")
        }
      }

    }

    "can be compared for equality with another Sentence" - {

      "in expected behaviour" in {
        forAll(nonEmptyStringGenerator) { str : String =>
          Sentence(str) should equal (Sentence(str))
        }
        forAll(nonEmptyStringGenerator, nonEmptyStringGenerator)((str1 : String, str2  : String) =>
          whenever(str1.toLowerCase != str2.toLowerCase) {
            Sentence(str1) should not equal (Sentence(str2))
          }
        )
      }

      "case-insensitively" in {
        forAll(nonEmptyStringGenerator) { str : String =>
          Sentence(str.toLowerCase) should equal (Sentence(str.toUpperCase))
        }
      }

    }

  }

}

