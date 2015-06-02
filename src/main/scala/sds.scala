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

}
