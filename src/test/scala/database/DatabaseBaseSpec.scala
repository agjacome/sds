package es.uvigo.esei.tfg.smartdrugsearch.database

import play.api.db.slick.Session
import play.api.test.WithApplication

import org.scalatest.BeforeAndAfter

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

trait DatabaseBaseSpec extends BaseSpec with BeforeAndAfter {

  lazy val dbProfile = DatabaseProfile()

  implicit var dbSession : Session = _

  before {
    new WithApplication {
      dbSession = DatabaseProfile.database.createSession()
      dbProfile.createTables()
    }
  }

  after {
    dbProfile.dropTables()
    dbSession.close()
  }

}

