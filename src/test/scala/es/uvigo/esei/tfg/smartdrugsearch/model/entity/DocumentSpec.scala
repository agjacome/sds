package es.uvigo.esei.tfg.smartdrugsearch.model.entity

import org.scalatest._

class DocumentSpec extends FlatSpec with Matchers {

  // implicit conversions from Int -> Identifier and String -> Sentence
  import Identifier.Predef._
  import Sentence.Predef._

  "A Document" should "be just an Identifier, a Title and a Text" in {
    Document(1, "This is a title", "This is my nice text")
    Document(10, "title", "text")
  }

  it should "throw an IllegalArgumentException if a empty text is given" in {
    a [IllegalArgumentException] should be thrownBy { Document( 1, "title", "") }
    a [IllegalArgumentException] should be thrownBy { Document(10, "title", "") }
    a [IllegalArgumentException] should be thrownBy { Document(50, "title", "") }
  }

}

