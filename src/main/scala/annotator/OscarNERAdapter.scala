package es.uvigo.esei.tfg.smartdrugsearch.annotator

import scala.concurrent.Future

import es.uvigo.esei.tfg.smartdrugsearch.entity._
import es.uvigo.esei.tfg.smartdrugsearch.database.dao._

private[annotator] class OscarNERAdapter extends NERAdapter {

  override final protected def annotate(document : Document) : Future[Finished] =
    ???

}

