package es.uvigo.esei.tfg.smartdrugsearch.controller

import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity._

private[controller] trait AccountsController extends Controller with Authorization {

  lazy val database = DatabaseProfile()

  import database._
  import database.profile.simple._

  def list(pageNumber : Option[Position], pageSize : Option[Size]) : Action[AnyContent] =
    AuthorizedAction() { _ => _ =>
      listResult(pageNumber getOrElse 1, pageSize getOrElse 50)
    }

  def get(id : AccountId) : Action[AnyContent] =
    AuthorizedAction() { _ => _ =>
      withAccount(id) { account => Ok(Json toJson account) }
    }

  def add : Action[JsValue] =
    AuthorizedAction(parse.json) { _ => request =>
      Account.form bind request.body fold (
        errors  => BadRequest(Json obj ("err" -> errors.errorsAsJson)),
        addResult
      )
    }

  def edit(id : AccountId) : Action[JsValue] =
    AuthorizedAction(parse.json) { _ => request =>
      Account.form bind request.body fold (
        errors  => BadRequest(Json obj ("err" -> errors.errorsAsJson)),
        account => editResult(id, account)
      )
    }

  def delete(id : AccountId) : Action[AnyContent] =
    AuthorizedAction() { _ => _ =>
      withAccount(id)(deleteResult)
    }

  private[this] def listResult(pageNumber : Position, pageSize : Size) =
    database withSession { implicit session =>
      val total  = Size(Accounts.count.toLong)
      val toTake = pageSize.toInt
      val toDrop = (pageNumber.toInt - 1) * toTake
      val list   = (Accounts drop toDrop take toTake).list
      Ok(Json toJson AccountList(total, pageNumber, pageSize, list))
    }

  private[this] def addResult(account : Account) =
    database withSession { implicit session =>
      if ((Accounts filter (_.email is account.email)).exists.run)
        BadRequest(Json obj ("err" -> "Email already exists in database"))
      else
        Created(Json obj ("id" -> (Accounts += account.hashPassword)))
    }

  private[this] def editResult(id : AccountId, account : Account) =
    withAccount(id) { _ =>
      database withSession { implicit session =>
        Accounts filter (_.id is id) map (_.password) update account.hashPassword.password
        NoContent
      }
    }

  private[this] def deleteResult(account : Account) =
    database withSession { implicit session =>
      if (Accounts.count > 1) {
        Accounts -= account
        NoContent
      } else BadRequest(Json obj ("err" -> "Cannot delete all accounts"))
    }

  private[this] def withAccount(id : AccountId)(f : Account => Result) =
    database withSession { implicit session =>
     (Accounts findById id).firstOption map f getOrElse NotFound(Json obj ("err" -> "Account not found"))
    }

}

object AccountsController extends AccountsController

