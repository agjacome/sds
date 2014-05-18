package es.uvigo.esei.tfg.smartdrugsearch

import play.api._

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.service._

object Global extends GlobalSettings {

  // force load of NER libraries, as they have a quite long startup time, and
  // services just load them lazily (which is bad for testing, because it slows
  // down the first action performed on them)
  override def beforeStart(app : Application) = {
    ABNERService.abner getEntities ""
    LinnaeusService.linnaeus `match` ""
    OscarService.oscar findResolvableEntities "toluene"
  }

  override def onStart(app : Application) =
    createTables(DatabaseProfile())

  private def createTables(database : DatabaseProfile) =
    database withSession { implicit session =>
      if (!database.isDatabaseEmpty) database.dropTables()
      database.createTables()
    }

}

