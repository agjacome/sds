package es.uvigo.esei.tfg.smartdrugsearch.database

import scala.slick.jdbc.meta.MTable
import play.api.db.slick.Profile

private[database] trait Queries { this : Profile with Tables with Mappers =>

  import profile.simple._

  object Documents extends TableQuery(new DocumentsTable(_)) {
    lazy val findById       = this findBy (_.id)
    lazy val findByPubMedId = this findBy (_.pubmedId)
  }

  object Keywords extends TableQuery(new KeywordsTable(_)) {
    lazy val findById         = this findBy (_.id)
    lazy val findByCategory   = this findBy (_.category)
    lazy val findByNormalized = this findBy (_.normalized)
  }

  object Annotations extends TableQuery(new AnnotationsTable(_)) {
    lazy val findById         = this findBy (_.id)
    lazy val findByDocumentId = this findBy (_.documentId)
    lazy val findByKeywordId  = this findBy (_.keywordId)
  }

  lazy val DocumentStats = TableQuery[DocumentStatsTable]

  lazy val DDL = Documents.ddl ++ Keywords.ddl ++ Annotations.ddl ++ DocumentStats.ddl

  def isDatabaseEmpty(implicit session : Session) : Boolean =
    MTable.getTables.list.isEmpty

  def createTables( )(implicit session : Session) : Unit =
    DDL.create

  def dropTables( )(implicit session : Session) : Unit =
    DDL.drop

}

