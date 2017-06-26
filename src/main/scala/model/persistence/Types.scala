package model.persistence

import java.util.UUID

object Types {
  type OptionLong   = Option[Long]
  type OptionString = Option[String]
  type OptionUuid   = Option[UUID]
}
