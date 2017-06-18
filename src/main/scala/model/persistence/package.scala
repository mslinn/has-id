package model

/** Using raw types such as `Long`, `java.util.UUID`, and `Option[Long]` for database ids invites errors.
  * Scala developers should instead use the [[Id]] and [[HasId]] wrapper types provided by this project
  * because of the type safety they provide over raw types.
  * `Id` and `HasId` are database-agnostic.
  * Both auto-increment `Id`s and `Id`s whose value is defined before persisting them are supported.
  *
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
  * }}} */

package object persistence
