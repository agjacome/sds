package es.uvigo.esei.tfg.smartdrugsearch.model.annotator

import akka.actor.Actor

import es.uvigo.esei.tfg.smartdrugsearch.model.Document
import es.uvigo.esei.tfg.smartdrugsearch.model.database.DAL

private[annotator] trait NERAdapter extends Actor {

  protected val dal : DAL

  import dal._
  import dal.profile.simple._

  protected implicit var session : Session = _

  override final def receive = {
    case session  : Session  => this.session = session
    case document : Document => (check _ andThen annotate)(document)
  }

  private def check(document : Document) : Document = {
    require(document.id.isDefined       , "Document must have a defined Id")
    require(Documents contains document , "Document must be stored in Database")
    document
  }

  protected def annotate(document : Document) : Unit

}

