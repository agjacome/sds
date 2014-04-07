package es.uvigo.esei.tfg.smartdrugsearch.database

import play.api.db.slick.Profile

import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Category, Sentence }

private[database] trait Mappers { this : Profile =>

  import profile.simple._

  implicit val sentenceColumnType =
    MappedColumnType.base[Sentence, String](_.toString, Sentence(_))

  implicit lazy val categoryColumnType =
    MappedColumnType.base[Category, Int](_.id, Category(_))

}

