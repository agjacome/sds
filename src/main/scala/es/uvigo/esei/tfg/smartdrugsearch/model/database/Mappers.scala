package es.uvigo.esei.tfg.smartdrugsearch.model.database

import play.api.db.slick.Profile
import es.uvigo.esei.tfg.smartdrugsearch.model.Sentence
import es.uvigo.esei.tfg.smartdrugsearch.model.Category
import Category._

private[database] trait Mappers { this : Profile =>

  import profile.simple.{ MappedColumnType, intColumnType, stringColumnType }

  implicit lazy val sentenceColumnType =
    MappedColumnType.base[Sentence, String](_.toString, Sentence(_))

  implicit lazy val categoryColumnType =
    MappedColumnType.base[Category, Int](_.id, Category(_))

}

