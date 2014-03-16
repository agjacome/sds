package es.uvigo.esei.tfg.smartdrugsearch.model.database

import play.api.Application
import play.api.db.slick.DB
import play.api.test._
import play.api.test.Helpers._

import es.uvigo.esei.tfg.smartdrugsearch.BaseSpec
import es.uvigo.esei.tfg.smartdrugsearch.model._

class DALSpec extends BaseSpec {

  "The Data Access Layer" - {

    "should work with the Database as expected with simple queries" - {

      "CRUDing documents" in new WithApplication {
        val dal = DAL(DB("test").driver)

        import dal._
        import dal.profile.simple._

        DB("test") withSession { implicit session : Session =>
          info("creating documents table")
          Documents.ddl.create

          info("inserting documents")
          Documents ++= Seq(
            Document(None, "Title One",   "Text One"),
            Document(None, "Title Two",   "Text Two"),
            Document(None, "Title Three", "Text Three")
          )

          info("retrieving all documents")
          Documents.list should have size (3)
          all (Documents.list map (_.id)) should be ('defined)
          Documents.contains(Document(Some(1), "Title One", "Text One")) should be (true)
          Documents.contains(Document(Some(1), "Title One", "Text One")) should be (true)
          Documents.contains(Document(Some(1), "Title One", "Text One")) should be (true)
          Documents.contains(Document(Some(1), "Title One", "Text One")) should be (true)

          info("searching for a concrete document")
          val second = (Documents filter (_.title is Sentence("Title Two"))).first
          second should have (
            'id    (Some(DocumentId(2))),
            'title (Sentence("Title Two")),
            'text  ("Text Two")
          )

          info("updating a document's information")
          (for {
            doc <- Documents
            if doc.title is Sentence("Title Three")
          } yield (doc.text)) update "The new and shiny third text"

          val third = (Documents filter (_.id is DocumentId(3))).first
          third.text should equal ("The new and shiny third text")

          info("deleting a document")
          (Documents filter (_.id is DocumentId(1))).delete
          (Documents filter (_.text is "Text One")).firstOption should be (None)

          info("removing documents table")
          Documents.ddl.drop
        }
      }

      "CRUDing named entities" in new WithApplication {
        val dal = DAL(DB("test").driver)

        import Category._
        import dal._
        import dal.profile.simple._

        DB("test") withSession { implicit session : Session =>
          info("creating entities table")
          NamedEntities.ddl.create

          info("inserting entities")
          NamedEntities ++= Seq(
            NamedEntity(None, "Normalized One",   Drug,      0),
            NamedEntity(None, "Normalized Two",   Species,  13),
            NamedEntity(None, "Normalized Three", Compound, 21)
          )

          info("retrieving all entities")
          NamedEntities.list should have size (3)
          all (NamedEntities.list map (_.id)) should be ('defined)

          info("searching for a concrete entity")
          val second = (NamedEntities filter (_.category is Species)).first
          second should have (
            'id          (Some(NamedEntityId(2))),
            'normalized  (Sentence("Normalized Two")),
            'category    (Species),
            'occurrences (13)
          )

          info("updating a named entity information")
          (for {
            ent <- NamedEntities
            if ent.normalized is Sentence("Normalized Three")
          } yield (ent.occurrences)) update 72

          val third = (NamedEntities filter (_.id is NamedEntityId(3))).first
          third.occurrences should be (72)

          info("deleting a named entity")
          (NamedEntities filter (_.id is NamedEntityId(1))).delete
          (NamedEntities filter (_.category is Drug)).firstOption should be (None)

          info("removing entities table")
          NamedEntities.ddl.drop
        }
      }

      "CRUDing annotations" in new WithApplication {
        val dal = DAL(DB("test").driver)

        import Category._
        import dal._
        import dal.profile.simple._

        DB("test") withSession { implicit session : Session =>
          info("creating annotations table (with dependencies in foreign keys)")
          dal.create

          info("inserting annotations (and dependent documents and entities)")
          Documents ++= Seq(
            Document(None, "Title One", "this is a non-normalized text one"),
            Document(None, "Title Two", "also non-normalized text 2")
          )
          NamedEntities ++= Seq(
            NamedEntity(None, "normalized-one", Drug, 1),
            NamedEntity(None, "normalized-two", Drug, 1)
          )
          Annotations ++= Seq(
            Annotation(None, 1, 1, "non-normalized text one", 10, 33),
            Annotation(None, 2, 2, "non-normalized text 2", 5, 26)
          )

          info("retrieving all annotations")
          Annotations.list should have size(2)
          all (Annotations.list map (_.id)) should be ('defined)

          info("searching for a concrete annotation")
          val second = (Annotations filter (_.text is Sentence("non-normalized text 2"))).first
          second should have (
            'id       (Some(AnnotationId(2))),
            'docId    (DocumentId(2)),
            'entId    (NamedEntityId(2)),
            'text     (Sentence("non-normalized text 2")),
            'startPos (5),
            'endPos   (26)
          )

          info("updating an annotation's information")
          (for {
            annotation <- Annotations
            if annotation.start is Position(10)
          } yield (annotation.end)) update 13

          val first = (Annotations filter (_.id is AnnotationId(1))).first
          first.endPos should be (Position(13))

          info("deleting an annotation")
          (Annotations filter (_.id is AnnotationId(1))).delete
          (Annotations filter (_.docId is DocumentId(1))).firstOption should be (None)

          info ("removing annotations table (and all its dependencies)")
          dal.drop
        }
      }

    }

    "should be able to use queries involving ForeignKeys as expected" - {

      "obtaining Documents directly from the FK of Annotations" in (pending)

      "obtaining NamedEntities directly from the FK of Annotations" in (pending)

    }

    "should be able to use the implicit extensions to create more complex queries" - {

      "with the Documents table" in new WithApplication {
        val dal = DAL(DB("test").driver)

        import dal._
        import dal.profile.simple._

        DB("test") withSession { implicit session : Session =>
          Documents.ddl.create

          Documents ++= Seq(
            Document(None, "Title One",   "Text One"),
            Document(None, "Title Two",   "Text Two"),
            Document(None, "Title Three", "Text Three")
            )

          info("searching by ID")
          Documents.byId(DocumentId(1)).first should have (
            'id    (Some(DocumentId(1))),
            'title (Sentence("Title One")),
            'text  ("Text One")
            )

          info("searching by title")
          Documents.byTitle(Sentence("Title Two")).first should have (
            'id    (Some(DocumentId(2))),
            'title (Sentence("Title Two")),
            'text  ("Text Two")
            )

          info("searching by text")
          Documents.byText("Text Three").first should have (
            'id    (Some(DocumentId(3))),
            'title (Sentence("Title Three")),
            'text  ("Text Three")
            )

          info("filtering by title with an incomplete String")
          Documents.byTitleLike("three").map(_.id).first should be (DocumentId(3))

          info("filtering by text with an incomplete String")
          Documents.byTextLike("On").map(_.id).first should be (DocumentId(1))

          info("checking if table contains a document")
          val docOne = Document(Some(1), "Title One", "Text One")
          val docTwo = Document(Some(7), "Non-existent", "Invalid text")
          Documents.contains(docOne) should be (true)
          Documents.contains(docTwo) should be (false)

          Documents.ddl.drop
        }
      }

      "with the NamedEntities table" in (pending)

      "with the Annotations table" in (pending)

    }

  }

}

