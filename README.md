# has-id

[![Build Status](https://travis-ci.org/mslinn/has-id.svg?branch=master)](https://travis-ci.org/mslinn/has-id)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fhas-id.svg)](https://badge.fury.io/gh/mslinn%2Fhas-id)

Using raw types such as `Long`, `UUID`, and `Option[Long]` for database ids invites errors.
Scala developers should instead use the `Id` and `HasId` wrapper types provided by this project
because of the type safety they provide over raw types.
`Id` and `HasId` are database-agnostic.
Both auto-increment `Id`s and `Id`s whose value is defined before persisting them are supported.

## Id
`Id` can wrap `Long`, `UUID` and `String` values, and any of them can be optional.
The supported flavors of `Id` are: 

  * `Id[Long]` &ndash; maps to Postgres `BIGINT` or `BIGSERIAL`
  * `Id[UUID]` &ndash; [do not misuse](https://tomharrisonjr.com/uuid-or-guid-as-primary-keys-be-careful-7b2aa3dcb439)
  * `Id[String]`
  * `Id[Option[Long]]` &ndash; commonly used with autoincrement columns such as `BIGSERIAL`
  * `Id[Option[UUID]]`
  * `Id[Option[String]]`

`Id` is a Scala value object, which means there is little or no runtime cost for using it as compared to the value that it wraps.
In other words, there is no penalty for boxing and unboxing.

## Convenience Types
   For convenience, the following types are defined in `model.persistence.Types`:
   
   * `OptionLong`     &ndash; `Option[Long]`
   * `OptionString`   &ndash; `Option[String]`
   * `OptionUuid`     &ndash; `Option[UUID]`
   * `IdLong`         &ndash; `Id[Long]`
   * `IdString`       &ndash; `Id[String]`
   * `IdUuid`         &ndash; `Id[UUID]`
   * `IdOptionLong`   &ndash; `Id[Option[Long]`
   * `IdOptionString` &ndash; `Id[Option[String]]`
   * `IdOptionUuid`   &ndash; `Id[Option[UUID]]`

### Id.empty
`Id`s define a special value, called `empty`.
Each `Id` flavor has a unique value for `empty`.
FYI, the values for `empty` are:

  * `IdUuid.empty == new UUID(0, 0)`
  * `IdLong.empty == 0L`
  * `IdString.empty == ""`
  * `IdOptionUuid.empty = None`
  * `IdOptionLong.empty = None`
  * `IdOptionString.empty = None`

Depending on the context, you might need to provide type ascription when using `Id.empty`.
For example, `IdUuid.empty` or `IdOptionLong.empty`.

### Id.toOption
You can use the `Id.toOption` method to convert from an `IdLong` or `IdUuid` to `IdOptionLong` or `IdOptionUuid`.
```
scala> import model.persistence._
import model.persistence._

scala> Id(Option(123L)).toOption
res2: model.persistence.Id[_ >: Option[Long] with Option[Option[Long]]] = 123
```
Be sure to cast the result to the desired `Id` subtype, otherwise you'll get a weird unhelpful type:
```
scala> Id(Option(123L)).toOption.asInstanceOf[IdLong]
res3: model.persistence.Id[Long] = 123

scala> import java.util.UUID
import java.util.UUID

scala> Id(Option(UUID.randomUUID)).toOption.asInstanceOf[IdUuid]
res3: model.persistence.Id[java.util.UUID] = b4570530-14d0-47d6-9d8b-af3b58ed075a
```

## HasId
Each case class that uses `Id` to represent the persisted record id in the database must extend `HasId`.
`HasId` is a parametric type with two type parameters:
  * The first type parameter must match the name of the case class
  * The second type parameter must match the type of the `Id` for the case class.
For example: 
  * `HasId[MyCaseClass, Long]`
  * `HasId[MyCaseClass, UUID]`
  * `HasId[MyCaseClass, String]`
  * `HasId[MyCaseClass, OptionLong]` &ndash; Most commonly used flavor
  * `HasId[MyCaseClass, OptionUuid]`
  * `HasId[MyCaseClass, OptionString]`

## Usage Examples
Here are examples of using `Id` and `HasId`:
 
### Simple Example
```
/** A person can have at most one Dog. 
  * Because their Id is based on `OptionUuid`, those `Id`s do not always have `Some` value */
case class Person(
   age: Int,
   name: String,
   dogId: IdOptionLong,
   override val id: IdUuid = Id(UUID.randomUUID) // Id type (UUID) matches the HasId type (also UUID)
 ) extends HasId[Person, UUID]

/** Dogs are territorial. They ensure that no other Dogs are allowed near their FavoriteTrees.
  * Because the Ids for Dog and FavoriteTree are based on Option[Long] and not UUID, 
  * those Ids might have value None until they are persisted */
case class Dog(
  species: String,
  color: String,
  override val id: IdOptionLong = Id.empty
) extends HasId[Dog, OptionLong]
```

### HasId Sub-Subclasses
Subclasses of `HasId` subclasses should be parametric. 
In the following example, `Rateable` is an abstract class that subclasses `HasId`.
Notice that `Rateable` is parametric in `T`, and `HasId`'s first type parameter is also `T`:
```
abstract class Rateable[T](override val id: IdOptionLong) extends HasId[T, OptionLong]
```
The following two `Rateable` subclasses provide values for `T` that match the names of the derived classes:

```
case class Inquiry(
  title: String,
  body: String,
  userId: IdOptionLong,
  lectureId: IdOptionLong,
  override val id: IdOptionLong = Id.empty
) extends Rateable[Inquiry](id)

case class Recording(
  ohSlotId: IdOptionLong,
  transcript: String,
  active: Boolean = false,
  override val id: IdOptionLong = Id.empty
) extends Rateable[Recording](id)
```

## For More Information
See the [unit tests](https://github.com/mslinn/has-id/blob/master/src/test/scala/IdTest.scala#L32-L62) 
for more code examples and documentation.
For an example of `has-id` in a real Scala project, see [play-authenticated](https://github.com/mslinn/play-authenticated/).

## Installation
Both Scala 2.11 and Scala 2.12 are supported.
Add this to your project's `build.sbt`:

    resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"

    libraryDependencies += "com.micronautics" %% "has-id" % "1.2.7" withSources()

## Scaladoc
[Here](http://mslinn.github.io/has-id/latest/api/#model.persistence.package)

## Sponsor
This project is sponsored by [Micronautics Research Corporation](http://www.micronauticsresearch.com/),
the company that delivers online Scala and Play training via [ScalaCourses.com](http://www.ScalaCourses.com).
You can learn how this project works by taking the [Introduction to Scala](http://www.ScalaCourses.com/showCourse/40),
and [Intermediate Scala](http://www.ScalaCourses.com/showCourse/45) courses.

## License
This software is published under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
