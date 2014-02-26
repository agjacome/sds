package es.uvigo.esei.tfg.smartdrugsearch.model.entity

import org.scalatest._

import Category._
import Identifier.Predef._
import Position.Predef._
import Sentence.Predef._

class AnnotationSpec extends FlatSpec with Matchers {

  // Should be a stub/mock, but current version of ScalaMock does not have the
  // previous compiler plugin (for scala 2.9), so it cannot create them from
  // final classes. It is a work in progress, so maybe in a future this could be
  // replaced with just "stub[Document]", but currently this is what we got.
  private[this] lazy val testDoc = Document(1, "title", "original text")

  "An Annotation" should "have original and normalized sentences, start and end positions, category and referenced document" in {
    val noteOne = Annotation( 0, "original", "normalized", Drug   , testDoc, 0,  8)
    val noteTwo = Annotation(10, "text"    , "text"      , Species, testDoc, 9, 13)
  }

  it must "throw an IllegalArgumentException if Starting Position is less than Ending Position" in {
    a [IllegalArgumentException] should be thrownBy {
      Annotation(0, "original", "normalized", Drug, testDoc, 3, 0)
    }
    a [IllegalArgumentException] should be thrownBy {
      Annotation(1, "text", "text", Species, testDoc, 10, 1)
    }
  }

  it must "throw an IllegalArgumentException if Ending Position is greater than document size" in {
    a [IllegalArgumentException] should be thrownBy {
      Annotation(0, "original", "normalized", Drug, testDoc, 0, 100)
    }
    a [IllegalArgumentException] should be thrownBy {
      Annotation(0, "text", "text", Species, testDoc, 9, 14)
    }
  }

}

