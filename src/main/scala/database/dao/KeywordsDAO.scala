package es.uvigo.esei.tfg.smartdrugsearch.database.dao

import es.uvigo.esei.tfg.smartdrugsearch.database.DatabaseProfile
import es.uvigo.esei.tfg.smartdrugsearch.entity.{ Keyword, KeywordId, Sentence }

trait KeywordsDAO extends DAO[Keyword, KeywordId] {

  import databaseProfile.profile.simple.Session

  def findByNormalized(normalized : Sentence)(implicit session : Session) : Option[Keyword]

}

object KeywordsDAO extends (() => KeywordsDAO) {

  def apply : KeywordsDAO =
    new KeywordsDAOImpl

}

private class KeywordsDAOImpl extends KeywordsDAO {

  import databaseProfile._
  import databaseProfile.profile.simple._

  override def findById(id : KeywordId)(implicit session : Session) : Option[Keyword] =
    (Keywords filter (_.id is id)).firstOption

  def findByNormalized(normalized : Sentence)(implicit session : Session) : Option[Keyword] =
    (Keywords filter (_.normalized is normalized)).firstOption

  protected def insert(keyword : Keyword)(implicit session : Session) : Keyword = {
    val id = Some(Keywords returning (Keywords map (_.id)) += keyword)
    keyword copy id
  }

  protected def update(keyword : Keyword)(implicit session : Session) : Keyword = {
    Keywords filter (_.id is keyword.id.get) update (keyword)
    keyword
  }

  def delete(keyword : Keyword)(implicit session : Session) : Unit =
    (Keywords filter (_.id is keyword.id.get)).delete

}

