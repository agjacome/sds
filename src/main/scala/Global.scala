package es.uvigo.esei.tfg.smartdrugsearch

import akka.actor._

import play.api._
import play.api.db.slick.DB
import play.libs.Akka.system

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

object Global extends GlobalSettings {

  lazy val annotator = system.actorSelection(system / "Annotator")

  override def onStart(app : Application) : Unit =
    system.actorOf(Props[Annotator], "Annotator")

}

