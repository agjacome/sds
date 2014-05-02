package es.uvigo.esei.tfg.smartdrugsearch

import akka.actor._

import play.api._
import play.libs.Akka.system

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

object Global extends GlobalSettings {

  lazy val annotator = system.actorSelection(system / "Annotator")

  override def onStart(app : Application) = {
    createDatabaseTables()
    system.actorOf(Props[Annotator], "Annotator")
  }

  private def createDatabaseTables( ) =
    DatabaseProfile.database withSession { implicit session =>
      val dbProfile = DatabaseProfile()
      if (dbProfile.isDatabaseEmpty) {
        Logger.info("[DB] Creating database schema. DDL:\n" + (dbProfile.ddl.createStatements mkString "\n"))
        dbProfile.createTables
      }
    }

}

