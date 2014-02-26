package es.uvigo.esei.tfg.smartdrugsearch.model.entity

final case class Document (val id : Identifier, val title : Sentence, val text : String) {

  require(!text.isEmpty,  "A non-empty text must be provided")

}
