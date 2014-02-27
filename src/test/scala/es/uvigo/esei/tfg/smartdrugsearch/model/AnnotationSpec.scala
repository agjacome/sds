package es.uvigo.esei.tfg.smartdrugsearch.model

import org.scalatest._

import AnnotationId.Predef._
import Category._
import DocumentId.Predef._
import Position.Predef._
import Sentence.Predef._

class AnnotationSpec extends FlatSpec with Matchers {

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

  "An Annotation" should "hold its Annotation ID, original and normalized sentences, start and end positions, category and referenced Document ID" in {
    val annot1 = Annotation(None, "original text", "normalized text", Drug, 123, 0, 8)
    val annot2 = Annotation(Some(13), "text", "text", Species, 1, 1, 5)

    annot1.id     should be (None)
    annot2.id.get should be (AnnotationId(13))

    annot1.originalText should equal (Sentence("original text"))
    annot2.originalText should equal (Sentence("text"))

    annot1.normalizedText should equal (Sentence("normalized text"))
    annot2.normalizedText should equal (Sentence("text"))

    annot1.category should be (Drug)
    annot2.category should be (Species)

    annot1.documentId should be (DocumentId(123))
    annot2.documentId should be (DocumentId(1))

    annot1.startPosition should be (Position(0))
    annot2.startPosition should be (Position(1))

    annot1.endPosition should be (Position(8))
    annot2.endPosition should be (Position(5))
  }

  it should "throw an IllegalArgumentException if given Start Position is bigger than given Ending Position" in {
    a [IllegalArgumentException] should be thrownBy { Annotation(None, "asd", "asd", Drug, 1,   10,  0) }
    a [IllegalArgumentException] should be thrownBy { Annotation(None, "asd", "asd", Drug, 1,    1,  0) }
    a [IllegalArgumentException] should be thrownBy { Annotation(None, "asd", "asd", Drug, 1,   20, 15) }
    a [IllegalArgumentException] should be thrownBy { Annotation(None, "asd", "asd", Drug, 1, 1000,  9) }
  }

}

