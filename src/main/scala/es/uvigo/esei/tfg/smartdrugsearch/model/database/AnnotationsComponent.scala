package es.uvigo.esei.tfg.smartdrugsearch.model.database

import play.api.db.slick.Profile
import es.uvigo.esei.tfg.smartdrugsearch.model._

private[database] trait AnnotationsComponent {

  this : Profile with Mappers with DocumentsComponent with NamedEntitiesComponent =>

  import profile.simple._

  class AnnotationsTable(val tag : Tag) extends Table[Annotation](tag, "annotations") {

    import scala.slick.model.ForeignKeyAction._

    def id    = column[AnnotationId]("id", O.PrimaryKey, O.AutoInc)
    def docId = column[DocumentId]("document", O.NotNull)
    def entId = column[NamedEntityId]("named_entity", O.NotNull)
    def text  = column[Sentence]("text", O.NotNull)
    def start = column[Position]("start", O.NotNull)
    def end   = column[Position]("end", O.NotNull)

    private val docs = TableQuery[DocumentsTable]
    private val ents = TableQuery[NamedEntitiesTable]

    def document    = foreignKey("Document_FK", docId, docs)(_.id, Cascade, Cascade)
    def namedEntity = foreignKey("NamedEntity_FK", entId, ents)(_.id, Cascade, Cascade)

    def * = (id.?, docId, entId, text, start, end) <> (Annotation.tupled, Annotation.unapply)

  }

}

