package es.uvigo.ei.sing

import scala.concurrent.Future

import play.api.Application
import play.api.libs.concurrent.Execution.Implicits.defaultContext

package object sds {

  import entity._
  import database._

  def httpContext(implicit app: Application): String =
    app.configuration.getString("play.http.context") map {
      case path if path endsWith "/" => path
      case path                      => path
    } getOrElse "/"

  def defaultAdmin(implicit app: Application): User = {
    val email = app.configuration.getString("admin.email").getOrElse("admin@sds.sing.ei.uvigo.es")
    val passw = app.configuration.getString("admin.pass").getOrElse("sds_default_pass")
    User(None, email, passw)
  }

  def createDatabase(implicit app: Application): Future[Boolean] = {
    val database = new DatabaseDDL
    database.isDatabaseEmpty flatMap { isEmpty =>
      if (isEmpty) database.createTables.map(_ => isEmpty)
      else Future.successful(isEmpty)
    }
  }

}
