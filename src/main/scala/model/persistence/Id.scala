package model.persistence

import com.micronautics.HasValue

case class Id[T](value: T) extends HasValue[T]
