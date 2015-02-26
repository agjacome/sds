package es.uvigo.ei.sing.sds

import akka.actor._
import es.uvigo.ei.sing.sds.annotator.Annotator
import es.uvigo.ei.sing.sds.database.DatabaseProfile
import es.uvigo.ei.sing.sds.entity.Account
import es.uvigo.ei.sing.sds.service.{ComputeStats, DocumentStatsService}
import play.api.Play.current
import play.api.libs.concurrent.Akka.system
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Application, GlobalSettings}

import scala.concurrent.Future
import scala.concurrent.duration._

trait Global extends GlobalSettings {

  lazy val appRoot       = current.configuration.getString("application.context").getOrElse("")
  lazy val annotator     = system.actorOf(Props[Annotator], "Annotator")
  lazy val documentStats = system.actorOf(Props[DocumentStatsService], "DocumentStatsComputer")

  override def onStart(app : Application) : Unit = {
    createTables(app, DatabaseProfile())
    scheduleStatsService(app)
  }

  override def onError(request : RequestHeader, error : Throwable) =
    Future { InternalServerError(Json obj ("err" -> s"Server Error: ${error.getMessage}")) }

  override def onHandlerNotFound(request : RequestHeader) =
    Future {
      if (request.path == s"$appRoot/")
        MovedPermanently(s"$appRoot")
      else
        NotFound(Json obj ("err" -> s"Path not found: ${request.path}"))
    }

  override def onBadRequest(request : RequestHeader, error : String) =
    Future { BadRequest(Json obj ("err" -> s"Bad Request: $error")) }

  private def createTables(app : Application, database : DatabaseProfile) =
    database withSession { implicit session =>
      if (database.isDatabaseEmpty)    database.createTables()
      if (database.Accounts.count < 1) database.Accounts += getDefaultAdmin(app)
    }

  private def scheduleStatsService(app : Application) : Unit = {
    val delay    = app.configuration getMilliseconds "documentStats.initialDelay" map (_.milliseconds)
    val interval = app.configuration getMilliseconds "documentStats.interval"     map (_.milliseconds)
    system.scheduler.schedule(delay getOrElse 10.seconds, interval getOrElse 6.hours, documentStats, ComputeStats)
    ()
  }

  private def getDefaultAdmin(app : Application) = {
    val email = app.configuration getString "application.admin.email" getOrElse "admin@smartdrugsearch"
    val passw = app.configuration getString "application.admin.pass"  getOrElse "$2a$10$1nvpfxRdbsmmEms4O/YN9u.Evxm1eihFhB9bLm4Mzy71kbvkjgNpO"
    Account(None, email, passw)
  }

}

object Global extends Global

