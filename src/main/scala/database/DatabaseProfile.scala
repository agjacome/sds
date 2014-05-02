package es.uvigo.esei.tfg.smartdrugsearch.database

import play.api.db.slick.{ Database, DB, Profile }
import scala.slick.driver.JdbcProfile

class DatabaseProfile private (val profile : JdbcProfile) extends Profile with Mappers with Tables

object DatabaseProfile extends (() => DatabaseProfile) {

  private var _database : Option[Database] = None

  def apply( ) : DatabaseProfile =
    new DatabaseProfile(database.driver)

  def database : Database =
    _database getOrElse DB(play.api.Play.current)

  def database_=(database : Database) : Unit =
    _database = Some(database)

}

