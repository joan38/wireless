package com.goyeau.wireless

import scala.reflect.macros.blackbox

object Unmarshallers {

  def forType(c: blackbox.Context)(`type`: c.Type): c.Tree = {
    import c.universe._
    val unmarshallerType = typeOf[Unmarshaller[_]].typeSymbol
    q"implicitly[$unmarshallerType[${`type`}]]"
  }

  def of[T](c: blackbox.Context)(implicit tagT: c.TypeTag[T]): c.Expr[Unmarshaller[T]] = {
    import c.universe._
    c.Expr[Unmarshaller[T]](forType(c)(typeOf[T]))
  }
}

object Marshallers {

  def forType(c: blackbox.Context)(`type`: c.Type): c.Tree = {
    import c.universe._
    val marshallerType = typeOf[Marshaller[_]].typeSymbol
    q"implicitly[$marshallerType[${`type`}]]"
  }

  def of[T](c: blackbox.Context)(implicit tagT: c.TypeTag[T]): c.Expr[Marshaller[T]] = {
    import c.universe._
    c.Expr[Marshaller[T]](forType(c)(typeOf[T]))
  }
}
