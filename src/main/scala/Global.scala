package es.uvigo.esei.tfg.smartdrugsearch

import scala.concurrent.duration._
import akka.actor._

import play.api.{ Application, GlobalSettings }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Akka.system

import es.uvigo.esei.tfg.smartdrugsearch.annotator.Annotator
import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.Account
import es.uvigo.esei.tfg.smartdrugsearch.service.{ ComputeStats, DocumentStatsService }

object Global extends GlobalSettings {

  import play.api.Play.current

  lazy val annotator     = system.actorOf(Props[Annotator], "Annotator")
  lazy val documentStats = system.actorOf(Props[DocumentStatsService], "DocumentStatsComputer")

  override def onStart(app : Application) = {
    createTables(app, DatabaseProfile())
    scheduleStatsService(app)
  }

  private def createTables(app : Application, db : DatabaseProfile) =
    db withSession { implicit session =>
      import db.profile.simple._

      if (db.isDatabaseEmpty) {
        db.createTables()
        db.Accounts += getDefaultAdmin(app)
      }
    }

  private def getDefaultAdmin(app : Application) = {
    val email = app.configuration getString "application.admin.email" getOrElse "admin@smartdrugsearch"
    val passw = app.configuration getString "application.admin.pass"  getOrElse "$2a$10$1nvpfxRdbsmmEms4O/YN9u.Evxm1eihFhB9bLm4Mzy71kbvkjgNpO"
    Account(None, email, passw)
  }

  private def scheduleStatsService(app : Application) = {
    val delay    = app.configuration getMilliseconds "documentStats.initialDelay" map (_.milliseconds)
    val interval = app.configuration getMilliseconds "documentStats.interval"     map (_.milliseconds)
    system.scheduler.schedule(
      delay getOrElse 10.seconds, interval getOrElse 6.hours, documentStats, ComputeStats
    )
  }

}

