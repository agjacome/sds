package es.uvigo.esei.tfg.smartdrugsearch.model.entity

import org.scalatest._

class DocumentSpec extends FlatSpec with Matchers {

  "A Document" should "be just an Identifier, a Title and a Text" in {
    Document(1, "This is a title", "This is my nice text")
    Document(10, "title", "text")
  }

  it should "throw an IllegalArgumentException if a negative integr is given as id" in {
    a [IllegalArgumentException] should be thrownBy { Document( -1, "title", "text") }
    a [IllegalArgumentException] should be thrownBy { Document(-10, "title", "text") }
    a [IllegalArgumentException] should be thrownBy { Document(-50, "title", "text") }
  }

  it should "throw an IllegalArgumentException if a empty title is given" in {
    a [IllegalArgumentException] should be thrownBy { Document( 1, "", "text") }
    a [IllegalArgumentException] should be thrownBy { Document(10, "", "text") }
    a [IllegalArgumentException] should be thrownBy { Document(50, "", "text") }
  }

  it should "throw an IllegalArgumentException if a empty text is given" in {
    a [IllegalArgumentException] should be thrownBy { Document( 1, "title", "") }
    a [IllegalArgumentException] should be thrownBy { Document(10, "title", "") }
    a [IllegalArgumentException] should be thrownBy { Document(50, "title", "") }
  }

}
