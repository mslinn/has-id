package model.persistence

import java.util.UUID

object Types {
  type OptionLong   = Option[Long]
  type OptionString = Option[String]
  type OptionUuid   = Option[UUID]

  type IdLong   = Id[Long]
  type IdString = Id[String]
  type IdUuid   = Id[UUID]

  type IdOptionLong   = Id[Option[Long]]
  type IdOptionString = Id[Option[String]]
  type IdOptionUuid   = Id[Option[UUID]]
}
