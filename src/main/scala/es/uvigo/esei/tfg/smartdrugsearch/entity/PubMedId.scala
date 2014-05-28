package es.uvigo.esei.tfg.smartdrugsearch.entity

final case class PubMedId (value : Long) extends AnyVal with Identifier
object PubMedId extends IdentifierCompanion[PubMedId]

