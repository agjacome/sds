package es.uvigo.esei.tfg.smartdrugsearch.model

import org.scalatest._

import DocumentId.Predef._
import Sentence.Predef._

class DocumentSpec extends FlatSpec with Matchers {

  "A DocumentID" should "be just a Long Integer value" in {
    DocumentId(0).id   should be (0)
    DocumentId(1).id   should be (1)
    DocumentId(-1).id  should be (-1)
    DocumentId(10).id  should be (10)
    DocumentId(-10).id should be (-10)
  }

  it can "be implicitly created from a Long Integer value given DocumentId.Predef is imported" in {
    val d1 : DocumentId = 0
    val d2 : DocumentId = 10
    val d3 : DocumentId = -10

    d1.id should be (0)
    d2.id should be (10)
    d3.id should be (-10)
  }

  it can "be implictly converted to a Long Integer value given DocumentId.Predef is imported" in {
    val i1 : Long = DocumentId(0)
    val i2 : Long = DocumentId(10)
    val i3 : Long = DocumentId(-10)

    i1 should be (0)
    i2 should be (10)
    i3 should be (-10)
  }

  "A Document" should "be just an Identifier, a Title and a Text" in {
    val doc1 = Document(None    , "this is a new doc"  , "this is the text of the new document")
    val doc2 = Document(Some(1) , "This is a title"    , "This is my nice text")
    val doc3 = Document(Some(10), "tnat is not a title", "this is my ugly text")

    doc1.id     should be (None)
    doc2.id.get should be (DocumentId(1))
    doc3.id.get should be (DocumentId(10))

    doc1.title should equal (Sentence("this is a new doc"))
    doc2.title should equal (Sentence("This is a title"))
    doc3.title should equal (Sentence("tnat is not a title"))

    doc1.text should equal ("this is the text of the new document")
    doc2.text should equal ("This is my nice text")
    doc3.text should equal ("this is my ugly text")
  }

  it should "throw an IllegalArgumentException if a empty text is given" in {
    a [IllegalArgumentException] should be thrownBy { Document(None    , "title0", "") }
    a [IllegalArgumentException] should be thrownBy { Document(Some(1) , "title1", "") }
    a [IllegalArgumentException] should be thrownBy { Document(Some(10), "title2", "") }
    a [IllegalArgumentException] should be thrownBy { Document(Some(50), "title3", "") }
  }

}

