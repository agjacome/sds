package es.uvigo.esei.tfg.smartdrugsearch.controller

import controllers._

// These objects exist for the sole purpose of redefining the Assets and
// WebJarAssets classes in the Play's "controllers" route package. This way, all
// controllers is normalized to exist inside the same package. This way, all
// those crazy inconsistencies of usign "controllers" and
// "es.uvigo.esei.tfg.smartdrugsearch.controller" in both the routes
// configuration file and in the views a removed.

object AssetsController       extends AssetsBuilder
object WebJarAssetsController extends WebJarAssets(Assets)

