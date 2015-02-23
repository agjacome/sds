package es.uvigo.ei.sing.sds.controller

import scala.concurrent.duration._
import scala.concurrent.Future

import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

import es.uvigo.ei.sing.sds.entity._

private[controller] trait Authorization { this : Controller =>

  implicit final class ResultWithToken (result : Result) {

    def addingToken(token : (String, AccountId)) : Result = {
      Cache set (token._1, token._2, sessionTimeout)
      result withCookies Cookie(authTokenCookieKey, token._1, None, httpOnly = false)
    }

    def discardingToken(token : String) : Result = {
      Cache remove token
      result discardingCookies DiscardingCookie(name = authTokenCookieKey)
    }

  }

  type Token = String

  implicit val app = play.api.Play.current

  lazy val authTokenHeader    = "X-XSRF-TOKEN"
  lazy val authTokenCookieKey = "XSRF-TOKEN"
  lazy val authTokenURLKey    = "auth"

  lazy val sessionTimeout = app.configuration getMilliseconds ("application.sessionTimeout") map {
    _.milliseconds.toSeconds.toInt
  } getOrElse 120

  def TokenizedAction[A](p : BodyParser[A] = parse.anyContent)(f : Token => AccountId => Request[A] => Result) =
    Action(p) {
      request => withToken(request)(f)
    }

  def AuthorizedAction[A](p : BodyParser[A] = parse.anyContent)(f : AccountId => Request[A] => Result) =
    Action(p) {
      request => withToken(request)(_ => f)
    }

  def AuthorizedAsyncAction[A](p : BodyParser[A] = parse.anyContent)(f : AccountId => Request[A] => Future[Result]) =
    Action.async(p) {
      request => withAsyncToken(request)(_ => f)
    }

  private[this] def withToken[A](request : Request[A])(f : Token => AccountId => Request[A] => Result) =
    tokenized(request)(f)(Unauthorized(Json obj ("err" -> "No authorization token")))

  private[this] def withAsyncToken[A](request : Request[A])(f : Token => AccountId => Request[A] => Future[Result]) =
    tokenized(request)(f)(Future { Unauthorized(Json obj ("err" -> "No authorization token")) })

  private[this] def tokenized[A, B](request : Request[A])(map : Token => AccountId => Request[A] => B)(orElse : B) =
    getAuthHeader(request) flatMap {
      token => Cache.getAs[AccountId](token) map { id => map(token)(id)(request) }
    } getOrElse orElse

  private[this] def getAuthHeader[A](request : Request[A]) =
    request.headers get authTokenHeader orElse (request getQueryString authTokenURLKey)

}

