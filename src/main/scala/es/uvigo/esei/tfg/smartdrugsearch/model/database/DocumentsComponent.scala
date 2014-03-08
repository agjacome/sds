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

}

