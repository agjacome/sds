package es.uvigo.esei.tfg.smartdrugsearch.annotator

import akka.actor.ActorSystem
import akka.testkit.TestKitBase

import play.api.db.slick.Session
import play.api.test.WithApplication

import org.scalatest.{ BeforeAndAfter, BeforeAndAfterAll }
import org.scalatest.prop.TableDrivenPropertyChecks

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

trait AnnotatorBaseSpec extends BaseSpec with TestKitBase
with BeforeAndAfter with BeforeAndAfterAll with TableDrivenPropertyChecks {

  // required because ImplicitSender is incompatible with TestKitBase trait, it
  // has a self-type with TestKit class: this has been fixed in Akka 2.3, but
  // that version is incompatible with Play 2.2.x, we're stuck with Akka 2.2.x
  // and this bug by now
  implicit def self = testActor

  lazy val system    = ActorSystem("testSystem")
  lazy val dbProfile = DatabaseProfile()

  implicit var dbSession : Session = _

  override def afterAll( ) : Unit =
    shutdown(system)

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

