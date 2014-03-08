package es.uvigo.esei.tfg.smartdrugsearch.model

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import Category._

class AnnotationSpec extends BaseSpec {

  "An Annotation" - {

    "can be constructed" - {
      "by using an Optional AnnotationId, a DocumentId, a NamedEntityId, a Sentence as original text and start and ending absoulte positions inside document's text" in {
        val annotOne = Annotation(None, 10, 13, Sentence.Empty, 0, 1)
        annotOne should have (
          'id       (None),
          'docId    (DocumentId(10)),
          'entId    (NamedEntityId(13)),
          'text     (Sentence.Empty),
          'startPos (0),
          'endPos   (1)
        )

        val annotTwo = Annotation(Some(17), 1, 2, "a sentence", 3, 9)
        annotTwo should have (
          'id       (Some(AnnotationId(17))),
          'docId    (DocumentId(1)),
          'entId    (NamedEntityId(2)),
          'text     (Sentence("a sentence")),
          'startPos (3),
          'endPos   (9)
        )
      }
    }

    "should throw an IllegalArgumentException" - {
      "when constructed with an Starting Position bigger than the Ending Position" in {
        a [IllegalArgumentException] should be thrownBy {
          Annotation(None, 0, 0, Sentence.Empty, 1, 0)
        }
        a [IllegalArgumentException] should be thrownBy {
          Annotation(Some(3), 1, 0, "a sentence", 1, 1)
        }
        a [IllegalArgumentException] should be thrownBy {
          Annotation(Some(5), 0, 3, "another sentence", 10, 5)
        }
      }
    }


  }

}

