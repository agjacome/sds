package es.uvigo.ei.sing.sds.searcher

import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future }

import play.api.Play.{ current => app }

import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.database.DatabaseProfile

class Searcher {

  lazy val database  = DatabaseProfile()
  lazy val searchers = createSearchers(app.configuration getStringList "searcher.searchers")

  import database._
  import database.profile.simple._

  def search(terms : Sentence, pageNumber : Position, pageSize : Size)(implicit ec : ExecutionContext) = {
    require(pageNumber >= 1, "Page Number must be equal or greater than one")
    require(pageSize   >= 0, "Page Size must be equal or greater than zero")
    getKeywords(terms) map { getSearchResults(_, pageNumber, pageSize) }
  }

  private[this] def getKeywords(terms : Sentence)(implicit ec : ExecutionContext) =
    Future sequence { searchers map (_ search terms) } map (_.flatten)

  private[this] def getSearchResults(keywords : Set[Keyword], pageNumber : Position, pageSize : Size) =
    SearchResultList(countDocuments(keywords), pageNumber, pageSize, getSearchResultList(
      queryDocuments(keywords, (pageNumber.toInt - 1) * pageSize.toInt, pageSize.toInt), keywords
    ))

  private[this] def getSearchResultList(documents : Seq[Document], keywords : Set[Keyword]) =
    documents map { document =>
      val ids = getKeywordIdsOfDocument(document, keywords flatMap (_.id))
      SearchResult(document, keywords filter { k => ids contains k.id.get })
    }

  private[this] def countDocuments(keywords : Set[Keyword]) =
    database withSession { implicit session =>
      groupDocumentStats(keywords flatMap (_.id)).length.run
    }

  private[this] def queryDocuments(keywords : Set[Keyword], first : Int, count : Int) =
    database withSession { implicit session =>
      (sortDocumentsByStats(keywords flatMap (_.id)) drop first take count).list
    }

  private[this] def getKeywordIdsOfDocument(document : Document, keywordIds : Set[KeywordId]) =
    database withSession { implicit session =>
      (Annotations filter {
        a => (a.documentId is document.id) && (a.keywordId inSet keywordIds)
      } map (_.keywordId)).list.toSet
    }

  private[this] def sortDocumentsByStats(keywordIds : Set[KeywordId])(implicit session : Session) =
    groupDocumentStats(keywordIds) sortBy { r => (r._2.desc, r._3.desc, r._4.desc) } map (_._1)

  private[this] def groupDocumentStats(keywordIds : Set[KeywordId])(implicit session : Session) =
    joinDocumentsWithStats(keywordIds) groupBy (_._2) map {
      case (doc, xs) => (doc, xs.map(_._1.ratio).sum, xs.length, xs.map(_._1.counter).sum)
    }

  private[this] def joinDocumentsWithStats(keywordIds : Set[KeywordId])(implicit session : Session) =
    DocumentStats filter (_.keywordId inSet keywordIds) join Documents on (_.documentId is _.id)

  private[this] def createSearchers(searchers : Option[java.util.List[String]]) =
    (searchers map (_.asScala) getOrElse List.empty).foldLeft(Set.empty[SearcherAdapter]) {
      case (set, clazz) => set + (Class forName clazz).newInstance.asInstanceOf[SearcherAdapter]
    }

}

object Searcher extends (() => Searcher) {

  def apply( ) : Searcher =
    new Searcher()

}

