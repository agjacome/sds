package es.uvigo.esei.tfg.smartdrugsearch.model

final object Category extends Enumeration {

  type Category = Value

  object Predef {

    import scala.language.implicitConversions
    import scala.slick.lifted.{ TypeMapper, MappedTypeMapper }

    implicit def stringToCategory(category : String) : Category =
      Category withName category

    implicit def categoryToString(category : Category) : String =
      category.toString

    implicit val categoryTypeMapper : TypeMapper[Category] =
      MappedTypeMapper.base[Category, String](categoryToString, stringToCategory)

  }

  val Compound = Value("compound")
  val Drug     = Value("drug")
  val Gene     = Value("gene")
  val Protein  = Value("protein")
  val Species  = Value("species")

}

