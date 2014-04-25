package es.uvigo.esei.tfg.smartdrugsearch

import play.api._
import play.api.db.slick.DB

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile

object Global extends GlobalSettings {

  override def onStart(app : Application) : Unit =
    DatabaseProfile setDefaultDatabase DB("test")(app)

}

