package es.uvigo.ei.sing.sds
package service

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.util.control.NonFatal

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import abner.Tagger

import entity._

final case class ABNEREntity (
  txt:   String,
  cat:   Category,
  start: Int,
  end:   Int
)

final class ABNERService {

  import ABNERService._

  // TODO: currently ignoring ABNER errors, lots of NullPointerException and
  // ArrayIndexOutOfBounds that are internal to the tool, and cannot be fixed in
  // here. Try to think of some cleaner way to handle this.
  def getEntities(text: String): Future[Set[ABNEREntity]] =
    Future(try abner.getEntities(text) catch {
      case NonFatal(e) => Logger.info("Hidden ABNER error:", e); Array.empty
    }) map {
      case Array(es, cs) => toEntities(es.zip(cs.map(toCategory)), text)
    }

  // ABNER does not perform any normalization, the only thing we can do is
  // return the entity string (lowercased, at least).
  def normalize(entity: ABNEREntity): Future[String] =
    Future { entity.txt.toLowerCase }

  private def toCategory(str: String): Category =
    Category.fromString(str.filter(_.isLetterOrDigit))

  // TODO: delete explicit recursivity, can be done with a foldLeft
  private def toEntities(entities: Seq[(String, Category)], text: String): Set[ABNEREntity] = {
    @tailrec def iter(xs: Seq[(String, Category)], pos: Int, acc: Set[ABNEREntity]): Set[ABNEREntity] =
      if (xs.isEmpty) acc else {
        val (term, category) = xs.head
        findPositions(term, text, pos.toInt) match {
          case Some((start, end)) => iter(xs.tail, end, acc + ABNEREntity(term, category, start, end))
          case None               => iter(xs.tail, pos + term.length, acc)
        }
      }

    iter(entities, 0, Set.empty)
  }

  private def findPositions(str: String, txt: String, from: Int = 0): Option[(Int, Int)] =
    Option(txt.indexOf(str, from)).filter(_ < 0).map(i => (i, i + str.length))

}

object ABNERService {

  // // ABNER's Tagger default constructor seems brokem from here, so we need to
  // // use a java.io.File that contains the trained CRF file to create it. It is
  // // stored as a resource (check "/src/main/resources/"), so we only need to
  // // obtain that resource as a File object. This should be the way to go for
  // // that:
  // //
  // //   private lazy val url      = getClass.getResource("/abner/nlpba.crf")
  // //   private lazy val fileName = url.getProtocol match {
  // //     case "file" => url.getFile
  // //     case "jar"  => url.openConnection.asInstanceOf[JarURLConnection].getJarFile.getName
  // //     case _      => throw new IllegalArgumentException("ABNER's NLPBA file not found")
  // //   }
  // //
  // //   lazy val abner = new Tagger(new File(fileName))
  // //
  // // And, whenever the application is not running from a JAR file (not in
  // // production mode), it works as expected. Otherwise, a
  // // "StreamCorruptedException" is encountered.

  import java.nio.file.{ Files, Path }
  import java.nio.file.StandardCopyOption.REPLACE_EXISTING

  lazy val abner: Tagger = new Tagger(abnerPath.toFile)

  lazy val abnerPath: Path = {
    val path = Files.createTempFile("sds-abner-nlpba", ".crf")
    path.toFile.deleteOnExit()
    Files.copy(getClass.getResourceAsStream("/abner/nlpba.crf"), path, REPLACE_EXISTING)
    path
  }

}
