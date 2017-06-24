package model.persistence

import com.micronautics.HasValue
import java.util.UUID

protected sealed class IdType[+T](val emptyValue: T)

protected object IdType {
  def apply[T](implicit idType: IdType[T]): IdType[T] = idType
  implicit object LongWitness   extends IdType[Long](0L)
  implicit object StringWitness extends IdType[String]("")
  implicit object UUIDWitness   extends IdType[UUID](new UUID(0L, 0L))

  // delegates to other IdTypes
  implicit def OptionWitness[T]( implicit contained: IdType[T] ): IdType[Option[T]]
    = new IdType[Option[T]]( None )
}

protected sealed class IdConverter[From, To: IdType](val convertValue: From => To)

protected object IdConverter {
  implicit def id[T: IdType]: IdConverter[T, T] = new IdConverter[T, T]( identity )
  implicit def option[From, To: IdType](
    implicit valueConverter: IdConverter[From, To]
  ): IdConverter[From, Option[To]] = new IdConverter[From, Option[To]](
    value => Some( valueConverter.convertValue(value) )
  )

  implicit object StringLong extends IdConverter[String, Long](_.toLong)
  implicit object StringUUID extends IdConverter[String, UUID](UUID.fromString)
  implicit object LongString extends IdConverter[Long, String](_.toString)
  implicit object LongUUID   extends IdConverter[Long, UUID](long => UUID.fromString(long.toString))
}

/** To use, either import `IdImplicits._` or mix in IdImplicitLike */
trait IdImplicitLike {
  implicit class ToId[From](from: From) {
    def toId[To: IdType](implicit converter: IdConverter[From, To]) = Id(converter.convertValue(from))
  }
}

object IdImplicits extends IdImplicitLike

object Id extends IdImplicitLike {
  def isEmpty[T](id: Id[T])(implicit idType: IdType[T]): Boolean = id.value == idType.emptyValue
  def empty[T](implicit idType: IdType[T]): Id[T] = Id(idType.emptyValue)

  def isValid[T: IdType](value: T): Boolean = try {
    Id(value)
    true
  } catch {
    case _: Exception => false
  }
}

case class Id[T: IdType](value: T) extends HasValue[T] {
  override def toString: String = value match {
    case Some(x) => x.toString

    //case None => "" // Scala compiler does not like this, so the following craziness is used:
    case n if n == None => ""

    case x => x.toString
  }
}

trait HasId[T, A] extends IdImplicitLike {
  def id: Id[A]

  def setId(newId: Id[A]): T = Copier.apply[T](this.asInstanceOf[T], "id" -> newId)
}
