package com.goyeau.wireless

trait Unmarshaller[T] {
  def apply(data: String): T
}
