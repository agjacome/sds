package es.uvigo.ei.sing.sds.database

import scala.slick.jdbc.meta.MTable
import play.api.db.slick.Profile

import es.uvigo.ei.sing.sds.entity._

private[database] trait Queries { this : Profile with Tables with Mappers =>

  import profile.simple._

  object Accounts extends TableQuery(new AccountsTable(_)) {

    lazy val findById    = this findBy (_.id)
    lazy val findByEmail = this findBy (_.email)

    def +=(account : Account)(implicit session : Session) : AccountId =
      this returning this.map(_.id) += account

    def -=(account : Account)(implicit session : Session) : Unit =
      account.id foreach { id => this -= id }

    def -=(id : AccountId)(implicit session : Session) : Unit = {
      (this findById id).delete
      ()
    }

    def update(account : Account)(implicit session : Session) : Unit = {
      this filter (_.id is account.id) update account
      ()
    }

    def count(implicit session : Session) : Int =
      this.length.run

  }

  object Documents extends TableQuery(new DocumentsTable(_)) {
    lazy val findById       = this findBy (_.id)
    lazy val findByPubMedId = this findBy (_.pubmedId)

    def +=(document : Document)(implicit session : Session) : DocumentId =
      this returning this.map(_.id) += document

    def -=(document : Document)(implicit session : Session) : Unit =
      document.id foreach { id => this -= id }

    def -=(documentId : DocumentId)(implicit session : Session) : Unit = {
      (this findById documentId).delete
      ()
    }

    def update(document : Document)(implicit session : Session) : Unit = {
      this filter (_.id is document.id) update document
      ()
    }

    def count(implicit session : Session) : Int =
      this.length.run

  }

  object Keywords extends TableQuery(new KeywordsTable(_)) {

    lazy val findById         = this findBy (_.id)
    lazy val findByCategory   = this findBy (_.category)
    lazy val findByNormalized = this findBy (_.normalized)

    def +=(keyword : Keyword)(implicit session : Session) : KeywordId =
      this returning this.map(_.id) += keyword

    def -=(keyword : Keyword)(implicit session : Session) : Unit =
      keyword.id foreach { id => this -= id }

    def -=(keywordId : KeywordId)(implicit session : Session) : Unit = {
      (this findById keywordId).delete
      ()
    }

    def update(keyword : Keyword)(implicit session : Session) : Unit = {
      this filter (_.id is keyword.id) update keyword
      ()
    }

    def count(implicit session : Session) : Int =
      this.length.run

  }

  object Annotations extends TableQuery(new AnnotationsTable(_)) {

    lazy val findById         = this findBy (_.id)
    lazy val findByDocumentId = this findBy (_.documentId)
    lazy val findByKeywordId  = this findBy (_.keywordId)

    def +=(annotation : Annotation)(implicit session : Session) : AnnotationId =
      this returning this.map(_.id) += annotation

    def -=(annotation : Annotation)(implicit session : Session) : Unit =
      annotation.id foreach { id => this -= id }

    def -=(annotationId : AnnotationId)(implicit session : Session) : Unit = {
      (this findById annotationId).delete
      ()
    }

    def update(annotation : Annotation)(implicit session : Session) : Unit = {
      this filter (_.id is annotation.id) update annotation
      ()
    }

    def count(implicit session : Session) : Int =
      this.length.run

  }

  object DocumentStats extends TableQuery(new DocumentStatsTable(_)) {
    lazy val findByDocumentId = this findBy (_.documentId)
    lazy val findByKeywordId  = this findBy (_.keywordId)
  }

  lazy val DDL = Accounts.ddl ++ Documents.ddl ++ Keywords.ddl ++ Annotations.ddl ++ DocumentStats.ddl

  def isDatabaseEmpty(implicit session : Session) : Boolean =
    MTable.getTables.list.isEmpty

  def createTables( )(implicit session : Session) : Unit =
    DDL.create

  def dropTables( )(implicit session : Session) : Unit =
    DDL.drop

}

