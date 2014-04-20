package es.uvigo.esei.tfg.smartdrugsearch.database

import scala.slick.driver.JdbcProfile
import play.api.db.slick.{ Database, DB, Profile }

class DatabaseProfile private (val database : Database) extends Profile with Mappers with Tables {

  val profile : JdbcProfile = database.driver

}

object DatabaseProfile extends (() => DatabaseProfile) {

  import play.api.Play.current

  private var database : Option[Database] = None

  def apply : DatabaseProfile =
    new DatabaseProfile(database getOrElse DB)

  def setDefaultDatabase(database : Database) : Unit =
    this.database = Some(database)

}

