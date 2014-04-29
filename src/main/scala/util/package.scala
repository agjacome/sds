package es.uvigo.esei.tfg.smartdrugsearch

package object util {

  import scalaxb.{ Soap11Clients, DispatchHttpClients }
  import scalaxb.generated.EUtilsServiceSoapBindings

  lazy val eUtilsService = (
    new EUtilsServiceSoapBindings with Soap11Clients with DispatchHttpClients { }
  ).service

}

