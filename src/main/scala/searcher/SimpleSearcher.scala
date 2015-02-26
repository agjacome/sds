package es.uvigo.ei.sing.sds.searcher

import scala.concurrent.{ ExecutionContext, Future }

import es.uvigo.ei.sing.sds.entity._

private[searcher] class SimpleSearcher extends SearcherAdapter {

  import database._
  import database.profile.simple._

  override def search(searchTerms : Sentence)(implicit ec : ExecutionContext) =
    Future { searchByWords(searchTerms) }

  private[this] def searchByWords(searchTerms : Sentence) =
    searchTerms.words.foldLeft(Set.empty[Keyword]) {
      case (set, word) => set ++ searchInAnnotationTexts(s"%$word%")
    }

  // the "groupBy identity map (_._1)" is used to simulate a SQL "SELECT
  // DISTINCT" since Slick does not have native support for it yet
  private[this] def searchInAnnotationTexts(searchPattern : String) =
    database withSession { implicit session =>
      (Annotations filter (_.text.asColumnOf[String] like searchPattern) flatMap {
        a => Keywords filter (_.id === a.keywordId)
      } groupBy identity map (_._1)).run
    }

}

private[searcher] object SimpleSearcher extends (() => SimpleSearcher) {

  def apply( ) : SimpleSearcher =
    new SimpleSearcher()

}

