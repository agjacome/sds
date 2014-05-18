package es.uvigo.esei.tfg.smartdrugsearch.annotator

import org.scalacheck.Arbitrary.arbitrary
import play.api.test.WithApplication

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

class AnnotatorMessageSpec extends BaseSpec {

  private[this] lazy val documentIdGenerator = arbitrary[Long] map DocumentId

  private[this] lazy val documentGenerator = for {
    title <- nonEmptyStringGenerator map Sentence
    text  <- nonEmptyStringGenerator
  } yield Document(None, title, text)

  "The Annotate message" - {

    "should be correctly created if its DocumentId is stored in the database" in new WithApplication {
      val database  = DatabaseProfile()
      implicit val session = database.createSession()

      import database._
      import database.profile.simple._

      forAll(documentGenerator) { (document : Document) =>
        val documentId = Documents returning Documents.map(_.id) += document
        Annotate(documentId) should have (
          'documentId (documentId.value)
        )
      }

      session.close()
    }

    "should throw an IllegalArgumentException if its DocumentId is not stored in the database" in new WithApplication {
      forAll(documentIdGenerator) { (documentId : DocumentId) =>
        a [IllegalArgumentException] should be thrownBy {
          val invalidMessage = Annotate(documentId)
        }
      }
    }

  }

}

