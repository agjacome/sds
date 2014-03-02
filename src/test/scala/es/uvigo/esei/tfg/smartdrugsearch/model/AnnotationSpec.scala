package es.uvigo.esei.tfg.smartdrugsearch.model

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import Category._
import AnnotationId.Predef._
import DocumentId.Predef._
import NamedEntityId.Predef._
import Position.Predef._
import Sentence.Predef._

class AnnotationSpec extends BaseSpec {

  "An AnnotationID" should "be just a Long Integer value" in {
    AnnotationId(0).id   should be (0)
    AnnotationId(1).id   should be (1)
    AnnotationId(-1).id  should be (-1)
    AnnotationId(10).id  should be (10)
    AnnotationId(-10).id should be (-10)
  }

  it can "be implicitly created from a Long Integer value given AnnotationId.Predef is imported" in {
    val a1 : AnnotationId = 0
    val a2 : AnnotationId = 10
    val a3 : AnnotationId = -10

    a1.id should be (0)
    a2.id should be (10)
    a3.id should be (-10)
  }

  it can "be implicitly converted to a Long Integer value given AnnotationId.Predef is imported" in {
    val i1 : Long = AnnotationId(0)
    val i2 : Long = AnnotationId(10)
    val i3 : Long = AnnotationId(-10)

    i1 should be (0)
    i2 should be (10)
    i3 should be (-10)
  }

  "An Annotation" should "hold its Annotation ID, NamedEntity ID, Document ID, original text and start/end positions" in {
    val annot1 = Annotation(None, 1, 2, "original text", 0, 8)
    val annot2 = Annotation(Some(3), 4, 5, "text", 0, 4)

    annot1.id       should be (None)
    annot2.id.value should be (AnnotationId(3))

    annot1.docId should be (DocumentId(1))
    annot2.docId should be (DocumentId(4))

    annot1.entId should be (NamedEntityId(2))
    annot2.entId should be (NamedEntityId(5))

    annot1.text should equal (Sentence("original text"))
    annot2.text should equal (Sentence("text"))

    annot1.startPos should be (Position(0))
    annot2.startPos should be (Position(0))

    annot1.endPos should be (Position(8))
    annot2.endPos should be (Position(4))
  }

  it should "throw an IllegalArgumentException if given Start Position is bigger than given Ending Position" in {
    a [IllegalArgumentException] should be thrownBy { Annotation(None, 1, 2, "asd",   10,  0) }
    a [IllegalArgumentException] should be thrownBy { Annotation(None, 1, 2, "asd",    1,  0) }
    a [IllegalArgumentException] should be thrownBy { Annotation(None, 1, 2, "asd",   20, 15) }
    a [IllegalArgumentException] should be thrownBy { Annotation(None, 1, 2, "asd", 1000,  9) }
  }

}

