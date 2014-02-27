package es.uvigo.esei.tfg.smartdrugsearch.model

import org.scalatest._

import Category._

class CategorySpec extends FlatSpec with Matchers {

  "A Category" can "be implicitly created from a String, given Category.Predef is imported" in {
    import Category.Predef._

    val drug    : Category = "drug"
    val species : Category = "species"

    drug    should be (Drug)
    species should be (Species)
  }

  it can "be created with withName passing it the right String" in {
    val drug    = Category withName "drug"
    val species = Category withName "species"

    drug    should be (Drug)
    species should be (Species)
  }

  it can "be implicitly converted back to a String, given Category.Predef is imported" in {
    import Category.Predef._

    val drug    : String = Drug
    val species : String = Species

    drug    should equal ("drug")
    species should equal ("species")
  }

  it can "be converted back to a string as expected with toString" in {
    val drug    = Category withName "drug"
    val species = Category withName "species"

    drug.toString    should equal ("drug")
    species.toString should equal ("species")
  }

  it should "throw a NoSuchElementException if a non-existent category is given as string" in {
    import Category.Predef._

    a [NoSuchElementException] should be thrownBy {
      Category withName "this_category_does_not_exist"
    }
    a [NoSuchElementException] should be thrownBy {
      val noCat : Category = "this_category_also_does_not_exist"
    }
  }

}

