package com.goyeau.wireless

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

object WirelessClient {

  def apply[Api[_[_]], F[_]](executor: (Seq[String], String) => F[String]): Api[F] =
    macro client[Api, F]

  def client[Api[_[_]], F[_]](
    c: blackbox.Context
  )(
    executor: c.Expr[(Seq[String], String) => F[String]]
  )(
    implicit apiTag: c.WeakTypeTag[Api[F]], typeTagF: c.WeakTypeTag[F[_]] // TODO check if we really need them
  ): c.Expr[Api[F]] = {
    import c.universe._

    val implementations = weakTypeOf[Api[F]].decls.collect {
      case symbol if Utils.isEffectMethod[Api, F](c)(symbol) =>
        val method = symbol.asMethod
        val params = method.paramLists.map(_.map { param =>
          ValDef(Modifiers(), param.name.toTermName, q"${param.asTerm.typeSignature}", EmptyTree)
        })
        def expr(param: Symbol) = q"""${param.name.decodedName.toString} -> ${Marshallers.forType(c)(param.typeSignature)}.apply(${param.asTerm})"""
        val payload = q"${Marshallers.of[Map[String, String]](c)}.apply(Map(..${method.paramLists.flatten.map(param => expr(param))}))"
        val returnType = method.returnType.typeArgs.head

        q"""override def ${method.name}[..${method.typeParams.map(_.typeSignature)}](...$params) =
              $executor(${Utils.path(c)(method)}, $payload).map(${Unmarshallers.forType(c)(returnType)}.apply)"""
    }
    val r = c.Expr[Api[F]](q"new ${weakTypeOf[Api[F]].typeSymbol}[${weakTypeOf[F[_]].typeConstructor}] { ..$implementations }")
    println("Client code:\n" + r)
    r
  }
}
