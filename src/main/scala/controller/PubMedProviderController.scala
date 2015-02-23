package es.uvigo.ei.sing.sds.controller

import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.json.{ Json, JsValue }

import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.provider.PubMedProvider

private[controller] trait PubMedProviderController extends Controller with Authorization {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  lazy val pubmed = PubMedProvider()

  def search(query : Sentence, limit : Option[Size], pageNumber : Position, pageSize : Size) : Action[AnyContent] =
    AuthorizedAsyncAction() { _ => _ =>
      pubmed.search(query, limit, pageNumber, pageSize) map {
        results => Ok(Json toJson results)
      } recover {
        case e : Throwable => InternalServerError(Json obj ("err" -> e.getMessage))
      }
    }

  def download( ) : Action[JsValue] =
    AuthorizedAsyncAction(parse.json) { _ => request =>
      (request.body \ "ids").validate[Set[PubMedId]] fold (
        errors => Future { BadRequest(Json obj ("err" -> errors.toString)) },
        downloadIds
      )
    }

  private[this] def downloadIds(ids : Set[PubMedId]) =
    pubmed download ids map {
      documentIds => Ok(Json toJson documentIds)
    }

}

object PubMedProviderController extends PubMedProviderController

