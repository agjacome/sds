package es.uvigo.esei.tfg.smartdrugsearch.database

import play.api.db.slick.{ Database, DB, Profile }
import scala.slick.driver.JdbcProfile

class DatabaseProfile private (val profile : JdbcProfile) extends Profile with Mappers with Tables

object DatabaseProfile extends (() => DatabaseProfile) {
 
  def apply( ) : DatabaseProfile =
    new DatabaseProfile(database.driver)

  def database : Database =
    DB(play.api.Play.current)

}

