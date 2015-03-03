package es.uvigo.ei.sing.sds

import scala.concurrent.duration._
import scala.concurrent.Future

import akka.actor._

import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }
import play.api.Play.{ current => app }
import play.api.{ Application, GlobalSettings, Logger, Play }

import es.uvigo.ei.sing.sds.annotator.Annotator
import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity.Account
import es.uvigo.ei.sing.sds.service.{ DocumentStatsService, ComputeStats }


object Global extends GlobalSettings {

  val annotator    = Akka.system.actorOf(Props[Annotator], "annotator")
  val statsService = Akka.system.actorOf(Props[DocumentStatsService], "stats_service")

  // TODO: replace mutable var with immutable counterpart
  var statsSchedule: Cancellable = _

  def context: String = {
    val path = app.configuration.getString("play.http.context").get
    if (path endsWith "/") path else path + "/"
  }

  def defaultAdmin: Account = {
    val mail = app.configuration.getString("application.admin.email").get
    val pass = app.configuration.getString("application.admin.pass" ).get
    Account(None, mail, pass)
  }

  def createDatabase(database: DatabaseProfile): Unit =
    database withSession { implicit session =>
      if (database.isDatabaseEmpty) {
        database.createTables()
        database.Accounts += defaultAdmin
        Logger.info(s"Created database tables and admin account ${defaultAdmin.email}")
      }
    }

  def scheduleStats(): Cancellable = {
    val delay    = app.configuration.getMilliseconds("stats.initialDelay").get.milliseconds
    val interval = app.configuration.getMilliseconds("stats.interval"    ).get.milliseconds
    Akka.system.scheduler.schedule(delay, interval, statsService, ComputeStats)
  }

  override def onStart(app: Application): Unit = {
    createDatabase(DatabaseProfile())
    this.statsSchedule = scheduleStats()
  }

  override def onError(request: RequestHeader, err: Throwable): Future[Result] = Future {
    InternalServerError(Json obj ("err" -> s"Server Error: ${err.getMessage}"))
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = Future {
    if (request.path == context)
      MovedPermanently(request.path dropRight 1)
    else
      NotFound(Json obj ("err" -> s"Path not found: ${request.path}"))
  }

  override def onBadRequest(request : RequestHeader, error : String): Future[Result] = Future {
    BadRequest(Json obj ("err" -> s"Bad Request: $error"))
  }

}
