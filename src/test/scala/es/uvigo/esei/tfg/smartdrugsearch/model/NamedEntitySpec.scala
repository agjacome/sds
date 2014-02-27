package es.uvigo.esei.tfg.smartdrugsearch.model

import org.scalatest._

import Category._
import NamedEntityId.Predef._
import Sentence.Predef._

class NamedEntitySpec extends FlatSpec with Matchers {

  "An NamedEntityID" should "be just a Long Integer value" in {
    NamedEntityId(0).id   should be (0)
    NamedEntityId(1).id   should be (1)
    NamedEntityId(-1).id  should be (-1)
    NamedEntityId(10).id  should be (10)
    NamedEntityId(-10).id should be (-10)
  }

  it can "be implicitly created from a Long Integer value given NamedEntityId.Predef is imported" in {
    val n1 : NamedEntityId = 0
    val n2 : NamedEntityId = 10
    val n3 : NamedEntityId = -10

    n1.id should be (0)
    n2.id should be (10)
    n3.id should be (-10)
  }

  it can "be implicitly converted to a Long Integer value given NamedEntityId.Predef is imported" in {
    val i1 : Long = NamedEntityId(0)
    val i2 : Long = NamedEntityId(10)
    val i3 : Long = NamedEntityId(-10)

    i1 should be (0)
    i2 should be (10)
    i3 should be (-10)
  }

  "A NamedEntity" should "hold its NamedEntity ID, normalized text sentence, category and a counter of occurrences" in {
    val nEnt1 = NamedEntity(None, "1S/C2H6O/c1-2-3/h3H,2H2,1H3", Compound)
    val nEnt2 = NamedEntity(Some(12), "ceftazidime", Drug, 291)
    val nEnt3 = NamedEntity(Some(23), "escherichia coli", Species , 182)

    nEnt1.id     should be (None)
    nEnt2.id.get should be (NamedEntityId(12))
    nEnt3.id.get should be (NamedEntityId(23))

    nEnt1.normalized should equal (Sentence("1S/C2H6O/c1-2-3/h3H,2H2,1H3"))
    nEnt2.normalized should equal (Sentence("ceftazidime"))
    nEnt3.normalized should equal (Sentence("escherichia coli"))

    nEnt1.category should be (Compound)
    nEnt2.category should be (Drug)
    nEnt3.category should be (Species)

    nEnt1.occurrences should be (0)
    nEnt2.occurrences should be (291)
    nEnt3.occurrences should be (182)
  }

  it should "throw an IllegalArgumentException if given occurrences is a negative integer" in {
    a [IllegalArgumentException] should be thrownBy { NamedEntity(None, "text", Drug, -1)  }
    a [IllegalArgumentException] should be thrownBy { NamedEntity(None, "text", Drug, -10) }
    a [IllegalArgumentException] should be thrownBy { NamedEntity(None, "text", Drug, -50) }
  }



}
