package model

/** Using raw types such as `Long`, `java.util.UUID`, and `Option[Long]` for database ids invites errors.
  * Scala developers should instead use the [[Id]] and [[HasId]] wrapper types provided by this project
  * because of the type safety they provide over raw types.
  * `Id` and `HasId` are database-agnostic.
  * Both auto-increment `Id`s and `Id`s whose value is defined before persisting them are supported.
  *
  * <h2>Id</h2>
  * `Id` can wrap `Long`, `UUID` and `String` values, and any of them can be optional.
  * The supported flavors of `Id` are:
  *
  *   - `Id[Long]` - maps to Postgres `BIGINT` or `BIGSERIAL`
  *   - `Id[UUID]` - [[https://tomharrisonjr.com/uuid-or-guid-as-primary-keys-be-careful-7b2aa3dcb439 do not misuse]]
  *   - `Id[String]`
  *   - `Id[Option[Long]]` - commonly used with autoincrement columns
  *   - `Id[Option[UUID]]`
  *   - `Id[Option[String]]`
  *
  * `Id` is a Scala value object, which means there is little or no runtime cost for using it as compared to the value that it wraps.
  * In other words, there is no penalty for boxing and unboxing.
  *
  * <h3>Id.empty</h3>
  * `Id`s define a special value, called `empty`.
  * Each `Id` flavor has a unique value for `empty`.
  * FYI, the values for `empty` are:
  *
  *   - `Id[UUID].empty == new UUID(0, 0)`
  *   - `Id[Long].empty == 0L`
  *   - `Id[String].empty == ""`
  *   - `Id[Option[UUID]].empty = None`
  *   - `Id[Option[Long]].empty = None`
  *   - `Id[Option[String]].empty = None`
  *
  * Depending on the context, you might need to provide type ascription when using `Id.empty`.
  * For example, `Id[UUID].empty` or `Id[Option[Long]].empty`.
  *
  * <h3>Id.toOption</h3>
  * You can use the `Id.toOption` method to convert from an `Id[Long]` or `Id[UUID]` to `Id[Option[Long]]` or `Id[Option[UUID]]`.
  * {{{
  * scala> import model.persistence._
  * import model.persistence._
  *
  * scala> Id(Option(123L)).toOption
  * res2: model.persistence.Id[_ >: Option[Long] with Option[Option[Long]]] = 123
  * }}}
  * Be sure to cast the result to the desired `Id` subtype, otherwise you'll get a weird unhelpful type:
  * {{{
  * scala> Id(Option(123L)).toOption.asInstanceOf[Id[Long]]
  * res3: model.persistence.Id[Long] = 123
  *
  * scala> import java.util.UUID
  * import java.util.UUID
  *
  * scala> Id(Option(UUID.randomUUID)).toOption.asInstanceOf[Id[UUID]]
  * res3: model.persistence.Id[java.util.UUID] = b4570530-14d0-47d6-9d8b-af3b58ed075a
  * }}}
  *
  * <h2>HasId</h2>
  * Each case class that uses `Id` to represent the persisted record id in the database must extend `HasId`.
  * The `HasId` type must match the type of the `Id` for the case class.
  * For example:
  *
  *   - `HasId[Long]`
  *   - `HasId[UUID]`
  *   - `HasId[String]`
  *   - `HasId[Option[Long]]`
  *   - `HasId[Option[UUID]]`
  *   - `HasId[Option[String]]`
  *
  * Here are examples of using `Id` and `HasId`:
  *
  * {{{
  * /** A person can have at most one Dog.
  *   * Because their Id is based on Option[UUID], those Ids do not always have a value */
  * case class Person(
  *    age: Int,
  *    name: String,
  *    dogId: Id[Option[Long]],
  *    override val id: Id[UUID] = Id(UUID.randomUUID) // Id type (UUID) matches the HasId type (also UUID)
  *  ) extends HasId[UUID]
  *
  * /** Dogs are territorial. They ensure that no other Dogs are allowed near their FavoriteTrees.
  *   * Because the Ids for Dog and FavoriteTree are based on Option[Long] and not UUID,
  *   * those Ids might have value None until they are persisted */
  * case class Dog(
  *   species: String,
  *   color: String,
  *   override val id: Id[Option[Long]] = Id.empty
  * ) extends HasId[Option[Long]]
  * }}}
  *
  * <h3>Convenience Types</h3>
  * For convenience, the following types are defined in [[Types]]:
  *   - `OptionLong`     &ndash; `Option[Long]`
  *   - `OptionString`   &ndash; `Option[String]`
  *   - `OptionUuid`     &ndash; `Option[UUID]`
  *   - `IdLong`         &ndash; `Id[Long]`
  *   - `IdString`       &ndash; `Id[String]`
  *   - `IdUuid`         &ndash; `Id[UUID]`
  *   - `IdOptionLong`   &ndash; `Id[Option[Long]`
  *   - `IdOptionString` &ndash; `Id[Option[String]]`
  *   - `IdOptionUuid`   &ndash; `Id[Option[UUID]]` */
package object persistence
