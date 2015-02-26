package es.uvigo.ei.sing.sds.entity

final case class PubMedId (value : Long) extends AnyVal with Identifier
object PubMedId extends IdentifierCompanion[PubMedId]

