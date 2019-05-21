package com.goyeau.wireless

import scala.reflect.macros.blackbox
import scala.language.higherKinds

object Utils {

  def path(c: blackbox.Context)(method: c.Symbol): c.Tree = {
    import c.universe._
    q"Seq(..${method.fullName.split("\\.").toSeq})"
  }

  def isEffectMethod[Api[_[_]], F[_]](c: blackbox.Context)(symbol: c.Symbol)(implicit apiTag: c.WeakTypeTag[Api[F]]): Boolean = {
    import c.universe._
    symbol.isMethod &&
      symbol.asMethod.returnType.typeConstructor.typeSymbol == weakTypeOf[Api[F]].typeParams.head.asType
  }
}
