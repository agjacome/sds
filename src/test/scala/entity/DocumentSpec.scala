package es.uvigo.esei.tfg.smartdrugsearch.entity

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec

class DocumentSpec extends BaseSpec {

  "A Document" - {

    "can be constructed" - {
      "by using an Optional DocumentId, a Sentence as title, a String as text, a Boolean as an annotated flag, and an Optional PubmedId" in {
        val docOne = Document(title = Sentence.Empty, text ="text")
        docOne should have (
          'id         (None),
          'title      (Sentence.Empty),
          'text       ("text"),
          'annotated  (false),
          'pubmedId   (None)
        )

        val docTwo = Document(Some(1), "my title", "my text document body", true, Some(123))
        docTwo       should have (
          'id        (Some(DocumentId(1))),
          'title     (Sentence("my title")),
          'text      ("my text document body"),
          'annotated (true),
          'pubmedId  (Some(PubmedId(123)))
        )
      }
    }

    "should throw an IllegalArgumentException" - {
      "when constructed when an empty text" in {
        a [IllegalArgumentException] should be thrownBy {
          val invalid : Document = Document(None, Sentence.Empty, "")
        }
      }
    }

  }

}

