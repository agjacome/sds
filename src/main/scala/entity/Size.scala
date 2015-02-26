package es.uvigo.ei.sing.sds.entity

import scala.slick.lifted.MappedTo

final class Size (val value : Long) extends AnyVal with Ordered[Size] with MappedTo[Long] {

  def compare(that : Size) : Int =
    value compare that.value

  def +(that : Size) : Size =
    Size(value + that.value)

  def -(that : Size) : Size =
    Size(value - that.value)

}

object Size extends (Long => Size) {

  import scala.language.implicitConversions
  import play.api.libs.json._
  import play.api.mvc.{ PathBindable, QueryStringBindable }

  def apply(size : Long) : Size = {
    require(size >= 0, "A Size must be a nonnegative Integer")
    new Size(size)
  }

  implicit def longToSize(size : Long) : Size = Size(size)
  implicit def sizeToLong(size : Size) : Long = size.value

  implicit val sizeWrites = Writes { (s : Size) => JsNumber(s.value) }
  implicit val sizeReads  = Reads.of[Long] map apply

  implicit def bindPath(implicit binder : PathBindable[Long]) : PathBindable[Size] =
    binder transform (apply, _.value)

  implicit def bindQuery : QueryStringBindable[Size] =
    QueryStringBindable.bindableLong transform (apply, _.value)

}
