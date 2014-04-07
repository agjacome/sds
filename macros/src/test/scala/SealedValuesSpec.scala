package es.uvigo.esei.tfg.smartdrugsearch.macros

import org.scalatest._

class SealedValuesSpec extends FreeSpec with Matchers {

  private[this] sealed trait MySealedTrait
  private[this] case object  MyFirstObject  extends MySealedTrait
  private[this] case object  MySecondObject extends MySealedTrait

  "The 'SealedValues.from' macro" - {

    "should return a Set with all the objects that extend a sealed trait" in {
      val values = SealedValues.from[MySealedTrait]
      values should contain theSameElementsAs Set(MyFirstObject, MySecondObject)
    }

  }

}
