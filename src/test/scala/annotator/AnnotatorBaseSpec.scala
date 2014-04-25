package es.uvigo.esei.tfg.smartdrugsearch.annotator

import akka.actor.ActorSystem
import akka.testkit.TestKitBase

import org.scalatest.BeforeAndAfterAll
import org.scalatest.prop.TableDrivenPropertyChecks

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseBaseSpec

trait AnnotatorBaseSpec extends DatabaseBaseSpec with TestKitBase
with BeforeAndAfterAll with TableDrivenPropertyChecks {

  // required because ImplicitSender is incompatible with TestKitBase trait, it
  // has a self-type with TestKit class: this has been fixed in Akka 2.3, but
  // that version is incompatible with Play 2.2.x, we're stuck with Akka 2.2.x
  // and this bug by now
  implicit def self = testActor

  lazy val system = ActorSystem("testSystem")

  override def afterAll : Unit =
    shutdown(system)

}

