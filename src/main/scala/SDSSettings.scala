package es.uvigo.ei.sing.sds

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{ Success, Failure }

import akka.actor._

import play.api.{ Application, GlobalSettings, Logger, Play }
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json

import entity._
import database._
import annotator._
import service._

object SDSSettings extends GlobalSettings {

  import Play.current

  lazy val annotator: ActorRef = Akka.system.actorOf(Props[Annotator], "annotator")
  lazy val indexer:   ActorRef = Akka.system.actorOf(Props[IndexerService], "indexer")

  lazy val indexerSchedule: Cancellable = {
    val delay    = current.configuration.getMilliseconds("indexer.initialDelay").getOrElse(500L).milliseconds
    val interval = current.configuration.getMilliseconds("indexer.interval"    ).getOrElse(3600000L).milliseconds
    Akka.system.scheduler.schedule(delay, interval, indexer, UpdateIndex)
  }

  override def onStart(app: Application): Unit =
    createDatabase(app) flatMap { created =>
      if (created) (new UsersDAO).insert(defaultAdmin(app)).map(_ => ()) else Future.successful(())
    } onComplete {
      case Success(_)   => annotator; indexer; indexerSchedule; () // Force evaluation of lazys
      case Failure(err) => Logger.error("Error while creating database", err); sys.exit(1)
    }

  override def onError(request: RequestHeader, err: Throwable): Future[Result] = 
    Future.successful {
      InternalServerError(Json obj ("err" -> s"Server Error: ${err.getMessage}"))
    }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] =
    Future.successful(
      if (request.path == httpContext)
        MovedPermanently(request.path.dropRight(1))
      else
        NotFound(Json.obj("err" -> s"Path not found: ${request.path}"))
    )

  override def onBadRequest(request : RequestHeader, error : String): Future[Result] =
    Future.successful(BadRequest(Json.obj("err" -> s"Bad Request: $error")))

}
