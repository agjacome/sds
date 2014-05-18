package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class SearchResult(
  document    : Document,
  annotations : Set[Annotation]
)

final case class SearchResults(
  result   : Set[SearchResult],
  keywords : Map[KeywordId, Keyword]
)
