package es.uvigo.esei.tfg.smartdrugsearch

import play.api.Logger

package object util {

  object EUtils {

    import scalaxb._
    import scalaxb.generated._

    lazy val service = (new EUtilsServiceSoapBindings with Soap11Clients with DispatchHttpClients { }).service

    def taxonomyScientificName(id : String) : Option[String] = {
      searchTaxonomy(id) match {
        case Left(fault)    => Logger.error(fault.toString); None
        case Right(summary) => parseScientificName(summary)
      }
    }

    private[this] def searchTaxonomy(id : String) : Either[Soap11Fault[Any], ESummaryResult] =
      service.run_eSummary(Some("taxonomy"), Some(id), None, None, None, None, None, None)

    private[this] def parseScientificName(summary : ESummaryResult) : Option[String] =
      (summary.DocSum flatMap (_.Item) filter (_.Name == "ScientificName")) match {
        case item :: _ => item.ItemContent
        case Nil       => None
      }

  }

}
