package es.uvigo.esei.tfg.smartdrugsearch

import akka.actor._

import play.api._
import play.libs.Akka.system

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

object Global extends GlobalSettings {

  lazy val annotator = system.actorOf(Props[Annotator], "Annotator")

  override def onStart(app : Application) =
    createTables(DatabaseProfile())

  private def createTables(database : DatabaseProfile) =
    database withSession { implicit session =>
      if (database.isDatabaseEmpty) database.createTables()
    }

}

