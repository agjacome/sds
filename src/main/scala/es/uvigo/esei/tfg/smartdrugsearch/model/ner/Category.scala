package es.uvigo.esei.tfg.smartdrugsearch.model.ner

final object Category extends Enumeration {

  type Category = Value

  object Predef {

    import scala.language.implicitConversions

    implicit def stringToCategory(category : String) : Category =
      Category withName category

  }

  val Compound = Value("compound")
  val Drug     = Value("drug")
  val Gene     = Value("gene")
  val Protein  = Value("protein")
  val Species  = Value("species")

}
