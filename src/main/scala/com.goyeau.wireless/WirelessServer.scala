package com.goyeau.wireless

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

object WirelessServer {

  def apply[Api[_[_]], F[_]](implementation: Api[F])(path: Seq[String], payload: String): F[String] =
    macro server[Api, F]

  def server[Api[_[_]], F[_]](
    c: blackbox.Context
  )(
    implementation: c.Expr[Api[F]]
  )(
    path: c.Expr[Seq[String]],
    payload: c.Expr[String]
  )(
    implicit typeTagF: c.WeakTypeTag[F[_]], apiFTag: c.WeakTypeTag[Api[F]] // TODO check if we really need them
  ): c.Expr[F[String]] = {
    import c.universe._

    val cases = weakTypeOf[Api[F]].decls.collect {
      case symbol if Utils.isEffectMethod[Api, F](c)(symbol)=>
        val method = symbol.asMethod
        val call = functionCall(c)(method, payload, implementation)
        val returnType = method.returnType.typeArgs.head

        cq"${Utils.path(c)(method)} => $call.map(${Unmarshallers.forType(c)(returnType)}.apply)"
    }.toSeq :+
      cq"""_ => throw new IllegalArgumentException()"""

    c.Expr[F[String]](q"""$path match { case ..$cases }""")
  }

  private def functionCall[Api[_[_]], F[_]](
    c: blackbox.Context
  )(method: c.universe.MethodSymbol, payload: c.Expr[String], implementation: c.Expr[Api[F]]) = {
    import c.universe._

    val methodName = c.Expr[String](Literal(Constant(method.fullName)))
    val argumentLists =
      method.paramLists.map(_.map { param =>
        val paramName = c.Expr[String](Literal(Constant(param.name.decodedName.toString)))
        val rawArgument = reify {
          Unmarshallers.of[Map[String, String]](c).splice // TODO extract this out to not execute the parsing x times
            .apply(payload.splice)
            .getOrElse(
              paramName.splice,
              throw new IllegalArgumentException(
                s"Parameter ${paramName.splice} for function ${methodName.splice} was not found in the request ${payload.splice}"
              )
            )
        }
        q"${Unmarshallers.forType(c)(param.typeSignature)}.apply($rawArgument)"
      })

    argumentLists.foldLeft(q"$implementation.$method") { (partiallyAppliedFunc, arguments) =>
      q"$partiallyAppliedFunc(..$arguments)"
    }
  }
}
