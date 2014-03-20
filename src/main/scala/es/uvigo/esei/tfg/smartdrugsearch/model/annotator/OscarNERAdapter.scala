package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

import scala.concurrent.Future

import es.uvigo.esei.tfg.smartdrugsearch.model.Document
import es.uvigo.esei.tfg.smartdrugsearch.model.database.{ DAL, current }

private[annotator] class OscarNERAdapter (protected val dal : DAL = current.dal) extends NERAdapter {

  override final protected def annotate(document : Document) : Future[FinishedAnnotation] =
    ???

}

