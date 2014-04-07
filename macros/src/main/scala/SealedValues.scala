package es.uvigo.esei.tfg.smartdrugsearch.macros

import scala.language.experimental.macros
import scala.reflect.macros.Context

object SealedValues {

  def from[A] : Set[A] = macro fromImpl[A]

  def fromImpl[A : c.WeakTypeTag](c : Context) = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol

    if (!symbol.isClass || !symbol.asClass.isSealed)
      c.abort(c.enclosingPosition, "Can only enumerate values of sealed trait or class")

    val children = symbol.asClass.knownDirectSubclasses.toList

    if (!children.forall(_.isModuleClass))
      c.abort(c.enclosingPosition, "All children must be objects")

    c.Expr[Set[A]] {
      def sourceModuleRef(sym : Symbol) =
        Ident(sym.asInstanceOf[
          scala.reflect.internal.Symbols#Symbol
        ].sourceModule.asInstanceOf[Symbol])

      Apply(Select(
        reify(Set).tree, newTermName("apply")
      ), children.map(sourceModuleRef(_)))
    }
  }

}
