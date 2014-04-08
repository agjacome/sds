package es.uvigo.esei.tfg.smartdrugsearch.annotator

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKitBase }

import org.scalatest.BeforeAndAfterAll
import org.scalatest.prop.TableDrivenPropertyChecks

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

trait AnnotatorBaseSpec extends BaseSpec with TestKitBase with ImplicitSender with BeforeAndAfterAll with TableDrivenPropertyChecks {

  implicit lazy val system = ActorSystem()

  override def afterAll : Unit =
    shutdown(system)

}

