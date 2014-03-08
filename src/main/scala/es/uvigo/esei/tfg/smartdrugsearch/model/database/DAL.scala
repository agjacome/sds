package es.uvigo.esei.tfg.smartdrugsearch.model.database

import scala.slick.driver.JdbcProfile
import play.api.db.slick.Profile
import play.api.db.slick.DB

case class DAL(override val profile : JdbcProfile) extends Profile with Mappers
with AnnotationsComponent with DocumentsComponent with NamedEntitiesComponent {

  import profile.DDL
  import profile.simple._

  val Documents     = TableQuery[DocumentsTable]
  val NamedEntities = TableQuery[NamedEntitiesTable]
  val Annotations   = TableQuery[AnnotationsTable]

  private lazy val ddl : DDL = Documents.ddl ++ NamedEntities.ddl ++ Annotations.ddl

  def create(implicit session : Session) : Unit =
    ddl.create

  def drop(implicit session : Session) : Unit =
    ddl.drop

}

object current { val dal = DAL(DB(play.api.Play.current).driver) }

