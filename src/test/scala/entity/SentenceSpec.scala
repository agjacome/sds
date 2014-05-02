package es.uvigo.esei.tfg.smartdrugsearch.entity

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class SentenceSpec extends BaseSpec {

  "A Sentence" - {

    "can be constructed" - {
      "implicitly from a String" in {
        val sentOne : Sentence = "this is a sentence"
        sentOne.words should be (Seq("this", "is", "a", "sentence"))

        val sentTwo : Sentence = "this is another sentence"
        sentTwo.words should be (Seq("this", "is", "another", "sentence"))
      }
      "explicitly by passing a 'String' to its constructor" in {
        val sentOne = Sentence("this is a sentence")
        sentOne.words should be (Seq("this", "is", "a", "sentence"))

        val sentTwo = Sentence("this is another different sentence")
        sentTwo.words should be (Seq("this", "is", "another", "different", "sentence"))
      }
      "as an empty Sentence by using its Empty object" in {
        val emptySentence = Sentence.Empty
        emptySentence.words should be ('empty)
      }
    }

    "can be converted to a 'String'" - {
      "implicitly" in {
        val sentOne : String = Sentence("this should be converted to a string")
        sentOne should be ("this should be converted to a string")

        val sentTwo : String = Sentence("this should also be converted to a string")
        sentTwo should be ("this should also be converted to a string")
      }
      "explicitly with the 'toString' method" in {
        val sentOne = Sentence("this should be converted to a string")
        sentOne.toString should be ("this should be converted to a string")

        val sentTwo = Sentence("this should also be converted to a string")
        sentTwo.toString should be ("this should also be converted to a string")
      }
      "explicitly with the 'mkString' method using a separator between words" in {
        val sentOne = Sentence("one two three four")
        (sentOne mkString "-") should be ("one-two-three-four")

        val sentTwo = Sentence("this should have percentages in between")
        (sentTwo mkString "%") should be ("this%should%have%percentages%in%between")
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
        val sentences = Seq(
          Sentence("  all   this spaces    should be     removed   "),
          Sentence("\nall\n\n\n\nthis\nspaces\nshould\n\n\n\nbe\nremoved\n"),
          Sentence("\tall\tthis\t\tspaces\t\t\t\t\t\t\t\tshould\t\tbe\tremoved\t\t\t")
        )

        forAll (sentences map (_.words)) {
          words => all (words) should not include regex("\\s")
        }

        val cleanedWords = Seq("all", "this", "spaces", "should", "be", "removed")
        all (sentences map (_.words)) should equal (cleanedWords)
      }
    }

    "can be compared for equality with another Sentence" - {
      "in expected behaviour" in {
        Sentence("a sentence")       should equal (Sentence("a sentence"))
        Sentence("another sentence") should equal (Sentence("another sentence"))

        Sentence("a sentence")       should not equal Sentence("another sentence")
        Sentence("another sentence") should not equal Sentence("a sentence")
      }
      "ignoring spaces between words" in {
        Sentence("  a \n\n sentence\t") should equal (Sentence("a sentence"))
        Sentence("another sentence")    should equal (Sentence("\t\t\tanother\n\nsentence\t\t\t"))
      }
      "case-insensitively" in {
        Sentence("A SENTENCE")       should equal (Sentence("a sentence"))
        Sentence("AnOtHeR SeNTEnCE") should equal (Sentence("aNoTHeR SEnteNCe"))
      }
    }

  }

}

