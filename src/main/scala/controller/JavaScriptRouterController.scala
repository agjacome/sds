package es.uvigo.esei.tfg.smartdrugsearch.controller

import play.api._
import play.api.mvc._

private[controller] trait JavaScriptRouterController extends Controller {

  def jsRoutes(varName : String = "jsRoutes") =
    Action { implicit request =>
      Ok(Routes.javascriptRouter(varName)(
        routes.javascript.ApplicationController.login,
        routes.javascript.ApplicationController.logout,
        routes.javascript.ApplicationController.authPing,
        routes.javascript.AccountsController.list,
        routes.javascript.AccountsController.get,
        routes.javascript.AccountsController.add,
        routes.javascript.AccountsController.edit,
        routes.javascript.AccountsController.delete,
        routes.javascript.DocumentsController.list,
        routes.javascript.DocumentsController.get,
        routes.javascript.DocumentsController.add,
        routes.javascript.DocumentsController.delete,
        routes.javascript.KeywordsController.list,
        routes.javascript.KeywordsController.get,
        routes.javascript.AnnotationsController.list,
        routes.javascript.AnnotationsController.get,
        routes.javascript.SearcherController.search,
        routes.javascript.AnnotatorController.annotate,
        routes.javascript.PubMedProviderController.search,
        routes.javascript.PubMedProviderController.download
      )) as JAVASCRIPT
    }

}

object JavaScriptRouterController extends JavaScriptRouterController

