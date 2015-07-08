package es.uvigo.ei.sing

import java.io.InputStream

import play.api.Application

package object sds {

  def resourceStream(resourceName: String): InputStream =
    getClass.getResourceAsStream(resourceName)

  def httpContext(implicit app: Application): String =
    app.configuration.getString("play.http.context") map {
      case path if path endsWith "/" => path
      case path                      => path + "/"
    } getOrElse "/"

  implicit class OptionOps[A](val opt: Option[A]) extends AnyVal {
    def getOrError(message: String): A =
      opt.getOrElse(sys.error(message))
  }

}
