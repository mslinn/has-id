package model.persistence

import java.util.UUID
import com.micronautics.HasValue
import scala.reflect.runtime.universe._

protected sealed class IdType[+T]( val emptyValue: T )
protected object IdType {
  def apply[T]( implicit idType: IdType[T] ): IdType[T] = idType
  implicit object LongWitness extends IdType[Long]( 0L )
  implicit object StringWitness extends IdType[String]( "" )
  implicit object UUIDWitness extends IdType[UUID]( new UUID(0L, 0L) )

  // delegates to other IdTypes
  implicit def OptionWitness[T]( implicit contained: IdType[T] ): IdType[Option[T]]
    = new IdType[Option[T]]( None )
}

/** To use, either import `IdImplicits._` or mix in IdImplicitLike */
trait IdImplicitLike {
  import Id.IdMix

  implicit class RichStringId(string: String) {
    def toId[A: TypeTag]: Id[_ >: IdMix] = string match {
      case _ if typeOf[A] <:< typeOf[String]       => Id(string)
      case _ if typeOf[A] <:< typeOf[Long]         => Id(string.toLong)
      case _ if typeOf[A] <:< typeOf[Option[Long]] => Id(Some(string.toLong))
      case _ if typeOf[A] <:< typeOf[UUID]         => Id(UUID.fromString(string))
    }
  }

  implicit class RichLongId(long: Long) {
    def toId[A: TypeTag]: Id[_ >: IdMix] = long match {
      case _ if typeOf[A] <:< typeOf[String]       => Id(long.toString)
      case _ if typeOf[A] <:< typeOf[Long]         => Id(long)
      case _ if typeOf[A] <:< typeOf[Option[Long]] => Id(Some(long))
      case _ if typeOf[A] <:< typeOf[UUID]         => Id(UUID.fromString(long.toString))
    }
  }

  implicit class RichIntId(int: Int) {
    def toId[A: TypeTag]: Id[_ >: IdMix] = int match {
      case _ if typeOf[A] <:< typeOf[String]       => Id(int.toString)
      case _ if typeOf[A] <:< typeOf[Long]         => Id(int.toLong)
      case _ if typeOf[A] <:< typeOf[Option[Long]] => Id(Some(int.toLong))
      case _ if typeOf[A] <:< typeOf[UUID]         => Id(UUID.fromString(int.toString))
    }
  }
}

object IdImplicits extends IdImplicitLike

object Id extends IdImplicitLike {
  type IdMix = String with Long with Option[Long] with UUID

  def isEmpty[T]( id: Id[T] )( implicit idType: IdType[T] ): Boolean = id.value == idType.emptyValue
  def empty[T]( implicit idType: IdType[T] ): Id[T] = Id( idType.emptyValue )

  def isValid[T: IdType](value: T): Boolean = try {
    Id(value)
    true
  } catch {
    case _: Exception => false
  }
}

case class Id[T: IdType](value: T) extends HasValue[T] {
  override def toString: String = value.toString
}

abstract class HasId[A: IdType] extends IdImplicitLike {
  import Id.IdMix
  def id: Id[A] = Id( IdType[A].emptyValue )
}
