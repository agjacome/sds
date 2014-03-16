package es.uvigo.esei.tfg.smartdrugsearch.model.database

import play.api.db.slick.Profile
import es.uvigo.esei.tfg.smartdrugsearch.model.{ Document, DocumentId, Sentence }

private[database] trait DocumentsComponent {

  this : Profile with Mappers =>

  import profile.simple._

  class DocumentsTable(val tag : Tag) extends Table[Document](tag, "documents") {

    def id    = column[DocumentId]("id", O.PrimaryKey, O.AutoInc)
    def title = column[Sentence]("title", O.NotNull)
    def text  = column[String]("abstract", O.NotNull, O.DBType("TEXT"))

    def * = (id.?, title, text) <> (Document.tupled, Document.unapply)

  }

  implicit class DocumentsExtension(val query : Query[DocumentsTable , Document]) {

    def byId(id : DocumentId) : Query[DocumentsTable, Document] =
      query filter (_.id is id)

    def byTitle(title : Sentence) : Query[DocumentsTable, Document] =
      query filter (_.title is title)

    def byText(text : String) : Query[DocumentsTable, Document] =
      query filter (_.text is text)

    def byTitleLike(filter : String = "%") : Query[DocumentsTable, Document] =
      query filter (_.title.asColumnOf[String] like s"%${filter}%")

    def byTextLike(filter : String = "%") : Query[DocumentsTable, Document] =
      query filter (_.text like s"%${filter}%")

    def contains(document : Document)(implicit session : Session) : Boolean =
      document.id.isDefined && byId(document.id.get).map(_.id).exists.run

  }

}

