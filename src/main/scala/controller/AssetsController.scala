package es.uvigo.ei.sing.sds.controller

import play.api.http.LazyHttpErrorHandler
import controllers._

// These objects exist for the sole purpose of redefining the Assets and
// WebJarAssets classes in the Play's "controllers" route package. This way, all
// controllers is normalized to exist inside the same package. This way, all
// those crazy inconsistencies of usign "controllers" and
// "es.uvigo.ei.sing.sds.controller" in both the routes
// configuration file and in the views a removed.

object AssetsController       extends AssetsBuilder(LazyHttpErrorHandler)
object WebJarAssetsController extends WebJarAssets(Assets)

