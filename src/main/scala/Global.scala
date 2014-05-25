package es.uvigo.esei.tfg.smartdrugsearch

import scala.concurrent.duration._
import scala.concurrent.Future

import akka.actor._

import play.api.Play.current
import play.api.{ Application, GlobalSettings }
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Akka.system
import play.api.libs.json.{ Json, JsValue }

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.Account
import es.uvigo.esei.tfg.smartdrugsearch.service.{ ComputeStats, DocumentStatsService }

object Global extends GlobalSettings {

  lazy val annotator     = system.actorOf(Props[Annotator], "Annotator")
  lazy val documentStats = system.actorOf(Props[DocumentStatsService], "DocumentStatsComputer")

  override def onStart(app : Application) = {
    createTables(app, DatabaseProfile())
    scheduleStatsService(app)
  }

  override def onError(request : RequestHeader, error : Throwable) =
    Future { InternalServerError(Json obj ("err" -> s"Server Error: ${error.getMessage}")) }

  override def onHandlerNotFound(request : RequestHeader) =
    Future { NotFound(Json obj ("err" -> s"Path not found: ${request.path}")) }

  override def onBadRequest(request : RequestHeader, error : String) =
    Future { BadRequest(Json obj ("err" -> s"Bad Request: $error")) }

  private def createTables(app : Application, database : DatabaseProfile) =
    database withSession { implicit session =>
      import database.profile.simple._
      if (database.isDatabaseEmpty)    database.createTables()
      if (database.Accounts.count < 1) database.Accounts += getDefaultAdmin(app)
    }

  private def scheduleStatsService(app : Application) = {
    val delay    = app.configuration getMilliseconds "documentStats.initialDelay" map (_.milliseconds)
    val interval = app.configuration getMilliseconds "documentStats.interval"     map (_.milliseconds)
    system.scheduler.schedule(delay getOrElse 10.seconds, interval getOrElse 6.hours, documentStats, ComputeStats)
  }

  private def getDefaultAdmin(app : Application) = {
    val email = app.configuration getString "application.admin.email" getOrElse "admin@smartdrugsearch"
    val passw = app.configuration getString "application.admin.pass"  getOrElse "$2a$10$1nvpfxRdbsmmEms4O/YN9u.Evxm1eihFhB9bLm4Mzy71kbvkjgNpO"
    Account(None, email, passw)
  }

}

