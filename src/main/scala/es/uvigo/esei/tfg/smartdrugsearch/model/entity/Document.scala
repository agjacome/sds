package es.uvigo.esei.tfg.smartdrugsearch.model.entity

case class Document (val id : Int, val title : String, val text : String) {

  require(id >= 0,        "Id must be a positive integer")
  require(!title.isEmpty, "A non-empty title must be provided")
  require(!text.isEmpty,  "A non-empty text must be provided")

}
