package es.uvigo.esei.tfg.smartdrugsearch.annotator

import akka.actor._
import play.api.Logger

import es.uvigo.esei.tfg.smartdrugsearch.entity.Document

class Annotator extends Actor {

  private lazy val annotators : Seq[ActorRef] = Seq(
    context.actorOf(Props[ABNERAdapter],       name = "ABNER"),
    context.actorOf(Props[LinnaeusNERAdapter], name = "Linnaeus"),
    context.actorOf(Props[OscarNERAdapter],    name = "Oscar")
  )

  override final def receive : Receive = {
    case document : Document     => annotate(document)
    case Finished(document)      => finished(sender, document)
    case Failed(document, cause) => failed(sender, document, cause)
  }

  private[this] def annotate(document : Document) : Unit =
    annotators foreach (_ ! Annotate(document))

  private[this] def finished(sender : ActorRef, document : Document) : Unit =
    Logger.info(s"[${sender.path.name}] Finished: ${document.title}")

  private[this] def failed(sender : ActorRef, document : Document, cause : Throwable) : Unit =
    Logger.error(s"[${sender.path.name}] Failed: ${document.title}\n${cause}")

}

