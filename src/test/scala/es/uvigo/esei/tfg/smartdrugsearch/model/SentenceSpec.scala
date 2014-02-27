package es.uvigo.esei.tfg.smartdrugsearch.model

import org.scalatest._

class SentenceSpec extends FlatSpec with Matchers {

  "A Sentence" should "be just a bunch of words" in {
    val sentence = Sentence("just a bunch of words")
    sentence.words should be (Seq("just", "a", "bunch", "of", "words"))
  }

  it must "remove all unnecessary spaces around words" in {
    val sentence = Sentence(" this   has   a\t\tlot        of  silly spaces     ")
    (sentence.words) should be (Seq("this", "has", "a", "lot", "of", "silly", "spaces"))
  }

  it must "throw an IllegalArgumentException if no word is given" in {
    a [IllegalArgumentException] should be thrownBy { Sentence("") }
  }

  it must "throw an IllegalArgumentException if just spaces are given" in {
    a [IllegalArgumentException] should be thrownBy { Sentence("  ")       }
    a [IllegalArgumentException] should be thrownBy { Sentence(" \t\t   ") }
    a [IllegalArgumentException] should be thrownBy { Sentence("\n")       }
  }

  it must "be equal to another if both share the same words" in {
    val sentenceOne = Sentence("this two should be equal")
    val sentenceTwo = Sentence("this two should be equal")
    sentenceOne should equal (sentenceTwo)
  }

  it must "have the same hashCode as another if both share the same words" in {
    val sentenceOne = Sentence("this two hashCodes should be equal")
    val sentenceTwo = Sentence("this two hashCodes should be equal")
    sentenceOne.hashCode should equal (sentenceTwo.hashCode)
  }

  it should "return its constructor phrase when calling toString" in {
    val phrase   = "This is a phrase"
    val sentence = Sentence(phrase)
    sentence.toString should equal (phrase)
  }

  it should "return a space-stripped version of its constructor phrase when calling toString" in {
    val sentence = Sentence("  This is     a    phrase\twith     silly   spaces       ")
    sentence.toString should equal ("This is a phrase with silly spaces")
  }

  it should "be implicitly converted from a String if Sentence.Predef is imported" in {
    import Sentence.Predef._
    val sentence : Sentence = "this should work"
  }

  it should "be implicitly converted to a String if Sentence.Predef is imported" in {
    import Sentence.Predef._
    val sentence : String = Sentence("this should also work")
  }

}

