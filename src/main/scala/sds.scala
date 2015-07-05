package es.uvigo.ei.sing

import play.api.Application

package object sds {

  def httpContext(implicit app: Application): String =
    app.configuration.getString("play.http.context") map {
      case path if path endsWith "/" => path
      case path                      => path
    } getOrElse "/"

}
