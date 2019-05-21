package com.goyeau.wireless

import scala.concurrent.ExecutionContext.Implicits.global
import com.goyeau.wireless.circe.CirceMarshallers._
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.FeatureSpec

import scala.concurrent.Future
import scala.language.higherKinds

trait Ahah[F[_]] {
  def toto(s: String, i: String): F[String]
  def tata(i: String): F[String]
}

class WirelessTests extends FeatureSpec with TypeCheckedTripleEquals {

  scenario("Client") {
    val client = WirelessClient[Ahah, Future]((path, payload) => Future.successful(""))
    client.tata("tota")
  }

  scenario("Server") {
    object AhahServer extends Ahah[Future] {
      override def toto(str: String, i: String): Future[String] = ???
      override def tata(i: String): Future[String] = ???
    }

    WirelessServer[Ahah, Future](AhahServer)(Seq.empty[String], "")
  }
}
