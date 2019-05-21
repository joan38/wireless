package com.goyeau.wireless.circe

import com.goyeau.wireless.{Marshaller, Unmarshaller}
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

object CirceMarshallers {
  implicit def encoder[T: Encoder] = new Marshaller[T] {
    override def apply(obj: T): String = obj.asJson.noSpaces
  }
  implicit def decoder[T: Decoder] = new Unmarshaller[T] {
    override def apply(data: String): T = io.circe.parser.decode[T](data).fold(throw _, identity)
  }
}
