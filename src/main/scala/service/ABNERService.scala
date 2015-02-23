package es.uvigo.ei.sing.sds.service

import scala.annotation.tailrec
import scala.concurrent.{ ExecutionContext, Future }

import abner.Tagger

import es.uvigo.ei.sing.sds.entity.{ Category, Position, Sentence }

final case class ABNEREntity(txt : Sentence, cat : Category, start : Position, end : Position)

class ABNERService private {

  import ABNERService.abner

  def getEntities(text : String)(implicit ec : ExecutionContext) : Future[Set[ABNEREntity]] =
    Future(abner getEntities text) map {
      case Array(entities, categories) => createEntities(entities zip (categories map toCategory), text)
    }

  // ABNER does not perform any normalization, so the only thing we can do by
  // now is return the entity string (lowercased). Could be replaced in a future
  // with something like Moara (http://moara.dacya.ucm.es/)
  def normalize(entity : ABNEREntity) : Sentence =
    entity.txt.toLowerCase

  private[this] def toCategory(str : String) =
    Category(str filter (_.isLetterOrDigit))

  private[this] def createEntities(entities : Seq[(String, Category)], text : String) = {
    @tailrec def iter(xs : Seq[(String, Category)], p : Position, acc : Set[ABNEREntity]) : Set[ABNEREntity] =
      if (xs.isEmpty) acc else {
        val (str, cat) = xs.head
        getStartAndEnd(str, text, p) match {
          case Some((start, end)) => iter(xs.tail, end, acc + ABNEREntity(str, cat, start, end))
          case None               => iter(xs.tail, p + str.length, acc)
        }
      }

    iter(entities, 0, Set.empty)
  }

  private[this] def getStartAndEnd(str : String, txt : String, from : Position = 0) = {
    val start = txt.indexOf(str, from.toInt)
    if (start < 0) None else Some((start, start + str.length))
  }

}

object ABNERService extends (() => ABNERService) {

  // ABNER's Tagger default constructor seems brokem from here, so we need to
  // use a java.io.File that contains the trained CRF file to create it. It is
  // stored as a resource (check "/src/main/resources/"), so we only need to
  // obtain that resource as a File object. This should be the way to go for
  // that:
  //
  //   private lazy val url      = getClass.getResource("/abner/nlpba.crf")
  //   private lazy val fileName = url.getProtocol match {
  //     case "file" => url.getFile
  //     case "jar"  => url.openConnection.asInstanceOf[JarURLConnection].getJarFile.getName
  //     case _      => throw new IllegalArgumentException("ABNER's NLPBA file not found")
  //   }
  //
  //   lazy val abner = new Tagger(new File(fileName))
  //
  // And, whenever the application is not running from a JAR file (not in
  // production mode), it works as expected. Otherwise, a
  // "StreamCorruptedException" is encountered. Maybe there is something in the
  // JAR packaging process of SBT/Play that corrupts the resource, I don't
  // really know and I don't have enough time to search for the real problem. So
  // a temporary solution is done here, where the resource is rewritten to
  // another file where we can safely create the File object (it's quite an
  // overhead to do this, note again that it is a TEMPORAL solution).

  import java.nio.file.Files
  import java.nio.file.StandardCopyOption.REPLACE_EXISTING

     lazy val abner     = new Tagger(abnerPath.toFile)
  private val abnerPath = createTemporalFile()

  private def createTemporalFile( ) = {
    val path = Files.createTempFile("sds-abner-nlpba", ".crf")
    path.toFile.deleteOnExit()
    Files.copy(getClass.getResourceAsStream("/abner/nlpba.crf"), path, REPLACE_EXISTING)
    path
  }

  def apply( ) : ABNERService =
    new ABNERService

}

