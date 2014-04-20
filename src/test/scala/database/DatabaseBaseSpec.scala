package es.uvigo.esei.tfg.smartdrugsearch.database

import play.api.db.slick.{ DB, Database, Session }
import play.api.test._

import org.scalatest.BeforeAndAfter

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

trait DatabaseBaseSpec extends BaseSpec with BeforeAndAfter {

  protected lazy val dbProfile = DatabaseProfile()

  protected implicit var dbSession : Session  = _

  before {
    new WithApplication {
      val database = DB("test")
      dbSession = database.createSession()

      DatabaseProfile setDefaultDatabase database
      dbProfile.createTables
    }
  }

  after {
    dbProfile.dropTables
    dbSession.close()
  }

}

