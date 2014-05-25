package es.uvigo.esei.tfg.smartdrugsearch.entity

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.github.t3hnar.bcrypt._

final case class AccountId (value : Long) extends AnyVal with Identifier
object AccountId extends IdentifierCompanion[AccountId]

final case class Account (id : Option[AccountId], email : String, password : String) {

  require(!email.isEmpty,    "The email cannot be empty")
  require(!password.isEmpty, "The password cannot be empty")

  def hashPassword : Account =
    copy(password = password bcrypt generateSalt)

  def checkPassword(password : String) : Boolean =
    password isBcrypted this.password

}

object Account extends ((Option[AccountId], String, String) => Account) {

  import play.api.data.Form
  import play.api.data.Forms._

  lazy val form = Form(mapping(
    "email"    -> email,
    "password" -> nonEmptyText
  )(formApply)(formUnapply))

  // do not write passwords!!
  implicit val accountWrites = (
    (__ \ 'id).writeNullable[AccountId] and
    (__ \ 'email).write[String]
  ) ((account : Account) => (account.id, account.email))

  implicit val accountReads = Json.reads[Account]

  private def formApply(email : String, password : String) : Account =
    apply(None, email, password)

  private def formUnapply(account : Account) : Option[(String, String)] =
    Some(account.email, account.password)

}

final case class AccountList (
  totalCount : Size,
  pageNumber : Position,
  pageSize   : Size,
  list       : Seq[Account]
) extends EntityList[Account]

object AccountList extends ((Size, Position, Size, Seq[Account]) => AccountList) {
  implicit val accountListWrites = Json.writes[AccountList]
}

