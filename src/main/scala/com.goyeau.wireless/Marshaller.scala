package com.goyeau.wireless

trait Marshaller[T] {
  def apply(obj: T): String
}
