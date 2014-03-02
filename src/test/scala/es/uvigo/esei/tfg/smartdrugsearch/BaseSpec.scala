package es.uvigo.esei.tfg.smartdrugsearch

import org.scalatest._
import org.scalamock.scalatest.MockFactory

abstract class BaseSpec
extends FlatSpec   with Matchers       with OptionValues
   with Inspectors with BeforeAndAfter // with MockFactory

