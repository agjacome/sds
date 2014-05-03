package es.uvigo.esei.tfg.smartdrugsearch.provider

import play.api.db.slick.{ DB, Session }
import play.api.test.WithApplication

import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._


class PubMedProviderSpec extends BaseSpec with ScalaFutures with BeforeAndAfter {

  implicit val patience = PatienceConfig(timeout = Span(5, Seconds))
  implicit var dbSession : Session = _

  private lazy val dbProfile = DatabaseProfile()

  before {
    new WithApplication {
      DatabaseProfile.database = DB("test")
      dbSession = DatabaseProfile.database.createSession()
      dbProfile.createTables()
    }
  }

  after {
    dbProfile.dropTables()
    dbSession.close()
  }

  import dbProfile.Documents
  import dbProfile.profile.simple._

  "The PubMed Provider" - {

    "can search in PubMed to retrieve a list of PubMedIDs" in {
      whenReady(PubMedProvider() search ("peptide", countPerPage = 10)) { res =>
        res.firstElement should be (Position(0))
        res.idList       should have size 10
      }
    }

    "can download articles from PubMed" in new WithApplication {
      val idList : Seq[PubMedId] = Seq(11035200, 21176972, 18803001)

      whenReady(PubMedProvider() download idList) { docs =>
        docs should contain theSameElementsAs (Documents map (_.id)).list
      }
    }

  }

}

