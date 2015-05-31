package es.uvigo.ei.sing.sds
package entity

import play.api.libs.json._

final case class SearchTerm (
  term:         String,
  tf:           Double,
  idf:          Double,
  tfidf:        Double,
  articleId:    Article.ID,
  keywordId:    Keyword.ID,
  annotationId: Annotation.ID
) {

  def id: SearchTerm.ID = (term, articleId, keywordId, annotationId)

}

object SearchTerm extends ((String, Double, Double, Double, Article.ID, Keyword.ID, Annotation.ID) => SearchTerm) {

  type ID = (String, Article.ID, Keyword.ID, Annotation.ID)

  implicit val SearchTermWrites: Writes[SearchTerm] = Json.writes[SearchTerm]

}
