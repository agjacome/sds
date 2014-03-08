package es.uvigo.esei.tfg.smartdrugsearch.model

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class DocumentSpec extends BaseSpec {

  "A Document" - {

    "can be constructed" - {
      "by using an Optional DocumentId, a Sentence as title, and a String as text" in {
        val docOne = Document(None, Sentence.Empty, "")
        docOne should have (
          'id    (None),
          'title (Sentence.Empty),
          'text  ("")
        )

        val docTwo = Document(Some(1), "my title", "my text document body")
        docTwo should have (
          'id    (Some(DocumentId(1))),
          'title (Sentence("my title")),
          'text  ("my text document body")
        )
      }
    }

  }

}

