package es.uvigo.ei.sing.sds.database

import play.api.db.slick.Profile

import es.uvigo.ei.sing.sds.entity._

private[database] trait Mappers { this : Profile =>

  import profile.simple._

  implicit lazy val sentenceColumnType = MappedColumnType.base[Sentence, String](_.toString, Sentence(_))
  implicit lazy val categoryColumnType = MappedColumnType.base[Category, CategoryId](_.id, Category(_))

}

