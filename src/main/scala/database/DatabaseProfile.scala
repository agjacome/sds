package es.uvigo.ei.sing.sds.database

import play.api.db.slick.{ Database, DB, Profile }

class DatabaseProfile private (
  val database : Database
) extends Profile with Mappers with Tables with Queries {

  override lazy val profile = database.driver

  def createSession( ) : profile.simple.Session =
    database.createSession()

  def withSession[A](block : profile.simple.Session => A) : A =
    database withSession { session => block(session) }

  def withTransaction[A](block : profile.simple.Session => A) : A =
    database withTransaction { session => block(session) }

}

object DatabaseProfile extends (() => DatabaseProfile) with ((Database) => DatabaseProfile) {

  def apply( ) : DatabaseProfile =
    new DatabaseProfile(DB(play.api.Play.current))

  def apply(database : Database) : DatabaseProfile =
    new DatabaseProfile(database)

}

