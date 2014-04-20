package es.uvigo.esei.tfg.smartdrugsearch.database

import play.api.db.slick.Profile

import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[database] trait Tables { this : Profile with Mappers =>

  import profile.DDL
  import profile.simple._

  class DocumentsTable(val tag : Tag) extends Table[Document](tag, "documents") {

    def id    = column[DocumentId]("id", O.PrimaryKey, O.AutoInc)
    def title = column[Sentence]("title", O.NotNull)
    def text  = column[String]("text", O.NotNull, O.DBType("TEXT"))

    def * = (id.?, title, text) <> (Document.tupled, Document.unapply)

  }

  class KeywordsTable(val tag : Tag) extends Table[Keyword](tag, "keywords") {

    def id          = column[KeywordId]("id", O.PrimaryKey, O.AutoInc)
    def normalized  = column[Sentence]("text", O.NotNull)
    def category    = column[Category]("category", O.NotNull)
    def occurrences = column[Long]("counter", O.Default(0))

    def * = (id.?, normalized, category, occurrences) <> (Keyword.tupled, Keyword.unapply)

  }

  class AnnotationsTable(val tag : Tag) extends Table[Annotation](tag, "annotations") {

    import scala.slick.model.ForeignKeyAction._

    def id    = column[AnnotationId]("id", O.PrimaryKey, O.AutoInc)
    def docId = column[DocumentId]("document", O.NotNull)
    def keyId = column[KeywordId]("keyword", O.NotNull)
    def text  = column[Sentence]("text", O.NotNull)
    def start = column[Position]("start", O.NotNull)
    def end   = column[Position]("end", O.NotNull)

    private val docs = TableQuery[DocumentsTable]
    private val keys = TableQuery[KeywordsTable]

    def document = foreignKey("Document_FK", docId, docs)(_.id, Cascade, Cascade)
    def keyword  = foreignKey("Keyword_FK", keyId, keys)(_.id, Cascade, Cascade)

    def * = (id.?, docId, keyId, text, start, end) <> (Annotation.tupled, Annotation.unapply)

  }

  lazy val Documents   = TableQuery[DocumentsTable]
  lazy val Keywords    = TableQuery[KeywordsTable]
  lazy val Annotations = TableQuery[AnnotationsTable]

  lazy val ddl : DDL = Documents.ddl ++ Keywords.ddl ++ Annotations.ddl

  def createTables(implicit session : Session) : Unit =
    ddl.create

  def dropTables(implicit session : Session) : Unit =
    ddl.drop

}

