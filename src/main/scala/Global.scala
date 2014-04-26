package es.uvigo.esei.tfg.smartdrugsearch

import akka.actor._

import play.api._
import play.libs.Akka.system

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator

object Global extends GlobalSettings {

  lazy val annotator = system.actorSelection(system / "Annotator")

  override def onStart(app : Application) : Unit =
    system.actorOf(Props[Annotator], "Annotator")

}

