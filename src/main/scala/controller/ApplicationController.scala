package es.uvigo.ei.sing.sds.controller

import play.api.Play.current
import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._
import play.twirl.api.Content

import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity._

private[controller] trait ApplicationController extends Controller with Authorization {

  lazy val appRoot   = current.configuration.getString("application.context").getOrElse("")
  lazy val database  = DatabaseProfile()

  import database._
  import database.profile.simple._

  def index(any : String) : Action[AnyContent] =
    Action {
      Ok(twirl.html.index())
    }

  def untrail(path : String) : Action[AnyContent] =
    Action {
      MovedPermanently(s"${appRoot}/${path}")
    }

  def login : Action[JsValue] =
    Action(parse.json) { request =>
      Account.form bind request.body fold (
        errors  => BadRequest(Json obj ("err" -> errors.errorsAsJson)),
        account => createLoginResponse(account.email, account.password)
      )
    }

  def authPing : Action[AnyContent] =
    TokenizedAction() { token => currentId => request =>
      if (doesAccountExist(currentId))
        Ok(Json obj ("accountId" -> currentId)) addingToken (token -> currentId)
      else NotFound(Json obj ("err" -> "Account not found"))
    }

  def logout : Action[AnyContent] =
    Action(_.headers get authTokenHeader map { token =>
        Ok discardingToken token
    } getOrElse BadRequest(Json obj ("err" -> "No token is set")))

  private[this] def createLoginResponse(email : String, password : String) =
    findByEmailAndPassword(email, password) map setToken getOrElse NotFound(
      Json obj ("err" -> "Account not found or password invalid.")
    )

  private[this] def setToken(account : Account) = {
    val token = java.util.UUID.randomUUID.toString
    Ok(Json obj (
      "authToken" -> token,
      "accountId" -> account.id.get
    )) addingToken (token -> account.id.get)
  }

  private[this] def doesAccountExist(id : AccountId) =
    database withSession { implicit session =>
      (Accounts filter (_.id === id)).exists.run
    }

  private[this] def findByEmailAndPassword(email : String, password : String) =
    database withSession { implicit session =>
      (Accounts findByEmail email).firstOption
    } filter (_.checkPassword(password))

}

object ApplicationController extends ApplicationController

