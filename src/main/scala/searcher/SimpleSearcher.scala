package es.uvigo.ei.sing.sds.searcher

import scala.concurrent.{ ExecutionContext, Future }
import scala.slick.jdbc.GetResult
import scala.slick.jdbc.StaticQuery.interpolation

import es.uvigo.ei.sing.sds.entity._

class SimpleSearcher extends SearcherAdapter {

  import database._
  import database.profile.simple._

  implicit val getKeywordResult: GetResult[Keyword] = GetResult(res => Keyword(
    Some(KeywordId(res.nextLong)), Sentence(res.nextString), Category(CategoryId(res.nextLong)), Size(res.nextLong)
  ))

  override def search(searchTerms: Sentence)(implicit ec: ExecutionContext): Future[Set[Keyword]] =
    Future { searchByWords(searchTerms) }

  private[this] def searchByWords(searchTerms: Sentence): Set[Keyword] =
    searchTerms.words.foldLeft(Set.empty[Keyword]) {
      case (set, word) => set ++ searchInAnnotationTexts(s"%$word%")
    }

  private[this] def searchInAnnotationTexts(searchPattern: String): Seq[Keyword] =
    database withSession { implicit session =>
      sql"""
        SELECT DISTINCT keyword_id, normalized_text, category, counter
        FROM annotations NATURAL JOIN keywords
        WHERE original_text LIKE $searchPattern
      """.as[Keyword].list
    }

}

object SimpleSearcher {
  def apply(): SimpleSearcher = new SimpleSearcher()
}

