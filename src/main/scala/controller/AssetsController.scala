package es.uvigo.ei.sing.sds
package controller

import play.api.http.LazyHttpErrorHandler

object AssetsController extends controllers.AssetsBuilder(LazyHttpErrorHandler)
