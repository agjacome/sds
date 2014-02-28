package es.uvigo.esei.tfg.smartdrugsearch.model.database

import org.virtuslab.unicorn.ids._
import scala.slick.session.Session

import es.uvigo.esei.tfg.smartdrugsearch.model.{ Document, DocumentId, Sentence }

object Documents extends IdTable[DocumentId, Document]("documents") {

  import Sentence.Predef._

  def title = column[Sentence]("title", O.NotNull)
  def text  = column[String]("abstract", O.NotNull, O.DBType("TEXT"))

  private def base = title ~ text

  override def * = id.? ~: base <> (Document.apply _, Document.unapply _)

  override def insertOne(doc : Document)(implicit session : Session) : DocumentId =
    saveBase(base, Document.unapply _)(doc)

}

