package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Keyword, KeywordId, Sentence }

trait KeywordsDAO extends DAO[Keyword, KeywordId] {

  import db.profile.simple.Session

  def findByNormalized(normalized : Sentence)(implicit session : Session) : Option[Keyword]

}

object KeywordsDAO extends (() => KeywordsDAO) with ((DatabaseProfile) => KeywordsDAO) {

  def apply : KeywordsDAO =
    new KeywordsDAOImpl

  def apply(db : DatabaseProfile) : KeywordsDAO =
    new KeywordsDAOImpl(db)

}

private class KeywordsDAOImpl (val db : DatabaseProfile = DatabaseProfile()) extends KeywordsDAO {

  import db._
  import db.profile._
  import db.profile.simple._

  private val keywords = TableQuery[KeywordsTable]

  override def findById(id : KeywordId)(implicit session : Session) : Option[Keyword] =
    (keywords where (_.id is id)).firstOption

  def findByNormalized(normalized : Sentence)(implicit session : Session) : Option[Keyword] =
    (keywords where (_.normalized is normalized)).firstOption

  protected def insert(keyword : Keyword)(implicit session : Session) : Keyword =
    keyword copy (id = Some(keywords returning (keywords map (_.id)) += keyword))

  protected def update(keyword : Keyword)(implicit session : Session) : Keyword = {
    keywords where (_.id is keyword.id.get) update (keyword)
    keyword
  }

  def delete(keyword : Keyword)(implicit session : Session) : Unit =
    (keywords where (_.id is keyword.id.get)).delete

}

