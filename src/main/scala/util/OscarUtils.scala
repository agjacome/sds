package es.uvigo.esei.tfg.smartdrugsearch.util

import scala.collection.JavaConversions._

import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.{ FormatType, ResolvedNamedEntity }

class OscarUtils private {

  import OscarUtils._

  def getNamedEntities(text : String) : Seq[ResolvedNamedEntity] =
    oscar.findResolvableEntities(text)

  def normalize(entity : ResolvedNamedEntity) : String =
    entity.getFirstChemicalStructure(FormatType.INCHI).getValue

  def normalize(entity : String) : String =
    normalizer.parseToInchi(entity)

}

object OscarUtils extends (() => OscarUtils) {

  import uk.ac.cam.ch.wwmm.oscar.Oscar
  import uk.ac.cam.ch.wwmm.opsin.NameToInchi

  lazy val oscar      = new Oscar()
  lazy val normalizer = new NameToInchi()

  def apply( ) : OscarUtils =
    new OscarUtils

}

