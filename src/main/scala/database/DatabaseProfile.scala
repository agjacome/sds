package es.uvigo.esei.tfg.smartdrugsearch.database

import scala.slick.driver.JdbcProfile
import play.api.db.slick.Profile

class DatabaseProfile private (val profile : JdbcProfile) extends Profile with Mappers with Tables

object DatabaseProfile extends (() => DatabaseProfile) {

  private var profile : Option[JdbcProfile] = None

  def apply : DatabaseProfile = {
    require(profile.isDefined, "A JDBC Profile must be set before using DatabaseProfile factory")
    new DatabaseProfile(profile.get)
  }

  def setProfile(profile : JdbcProfile) : Unit =
    this.profile = Some(profile)

}

