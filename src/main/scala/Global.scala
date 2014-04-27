package es.uvigo.esei.tfg.smartdrugsearch

import akka.actor._

import play.api._
import play.libs.Akka.system

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

object Global extends GlobalSettings {

  lazy val annotator = system.actorSelection(system / "Annotator")

  private lazy val dbProfile = DatabaseProfile()

  override def onStart(app : Application) : Unit = {
    createDatabaseTables()
    system.actorOf(Props[Annotator], "Annotator")
  }

  private def createDatabaseTables( ) : Unit =
    dbProfile.database withSession { implicit session =>
      if (dbProfile.isDatabaseEmpty) {
        Logger.info(
          "[DB] Database empty, creating required tables now. DDL:\n" +
          (dbProfile.ddl.createStatements mkString "\n")
        )
        dbProfile.createTables
      }
    }

}

