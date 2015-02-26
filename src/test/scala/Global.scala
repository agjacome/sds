package es.uvigo.ei.sing.sds

import play.api._

import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.service._

trait Global extends GlobalSettings {

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

object Global extends Global

