package es.uvigo.ei.sing.sds
package database

import scala.concurrent.Future

import play.api.Play
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfig }
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import slick.jdbc.meta.MTable
import slick.driver.JdbcProfile

final class DatabaseDDL extends AnnotationsComponent with ArticlesComponent with AuthorsComponent with KeywordsComponent with SearchTermsComponent with UsersComponent with HasDatabaseConfig[JdbcProfile] {

  import driver.api._

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  lazy val schema: driver.DDL =
    annotations.schema ++
    articles.schema    ++
    authors.schema     ++
    keywords.schema    ++
    terms.schema       ++
    users.schema

  def isDatabaseEmpty: Future[Boolean] =
    db.run(MTable.getTables).map(_.toList.isEmpty)

  def createTables: Future[Unit] =
    db.run(schema.create)

  def dropTables: Future[Unit] =
    db.run(schema.drop)

}
