package es.uvigo.esei.tfg.smartdrugsearch

import org.scalatest._
import org.scalatest.concurrent.{ Futures, ScalaFutures }
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.PropertyChecks
import org.scalatest.time.{ Seconds, Span }

import org.scalacheck.Gen

trait BaseSpec extends FreeSpec with Matchers with OptionValues
with PropertyChecks with Futures with ScalaFutures with MockitoSugar {

  // maximum wait time for futures to complete when using "whenReady"
  implicit override val patienceConfig = PatienceConfig(timeout = Span(20, Seconds))

  protected lazy val nonEmptyStringGenerator = Gen.alphaStr suchThat {
    str => str.trim split "\\s+" forall (!_.isEmpty)
  }

  protected def cleanSpaces(str : String) : String =
    str.stripMargin filter (_ >= ' ')

}

