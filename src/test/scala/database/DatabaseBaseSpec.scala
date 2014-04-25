package es.uvigo.esei.tfg.smartdrugsearch.database

import play.api.db.slick.{ DB, Session }
import play.api.test._

import org.scalatest.BeforeAndAfter

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

trait DatabaseBaseSpec extends BaseSpec with BeforeAndAfter {

  protected lazy val dbProfile = DatabaseProfile()

  protected implicit var dbSession : Session = _

  before {
    new WithApplication {
      dbSession = DB("test").createSession()
      dbProfile.createTables
    }
  }

  after {
    dbProfile.dropTables
    dbSession.close()
  }

}

