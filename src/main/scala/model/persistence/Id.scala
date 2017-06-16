package model.persistence

import java.util.UUID
import com.micronautics.HasValue
import scala.reflect.runtime.universe._

protected sealed trait IdType[T] {
  def empty: Id[T]
}

protected object IdType {
  implicit object LongWitness extends IdType[Long] {
    val empty: Id[Long] = Id(0L)
  }

  implicit object StringWitness extends IdType[String] {
    val empty: Id[String] = Id("")
  }

  implicit object UUIDWitness extends IdType[UUID] {
    val empty: Id[UUID] = Id(new UUID(0L, 0L))
  }
}

trait HasId extends IdImplicitLike {
  def id[T: IdType]: Id[_ >: IdMix] = Id.empty
}

trait MaybeHasId[T] extends IdImplicitLike {
  val id: Option[Id[T]] = None
}

/** To use, either import `IdImplicits._` or mix in IdImplicitLike */
trait IdImplicitLike {
  type IdMix = String with Long with UUID

  implicit class RichStringId(string: String) {
    def toId[A: TypeTag]: Id[_ >: IdMix] = string match {
      case _ if typeOf[A] <:< typeOf[String] => Id(string)
      case _ if typeOf[A] <:< typeOf[Long]   => Id(string.toLong)
      case _ if typeOf[A] <:< typeOf[UUID]   => Id(UUID.fromString(string))
    }
  }

  implicit class RichLongId(long: Long) {
    def toId[A: TypeTag]: Id[_ >: IdMix] = long match {
      case _ if typeOf[A] <:< typeOf[String] => Id(long.toString)
      case _ if typeOf[A] <:< typeOf[Long]   => Id(long)
      case _ if typeOf[A] <:< typeOf[UUID]   => Id(UUID.fromString(long.toString))
    }
  }

  implicit class RichIntId(int: Int) {
    def toId[A: TypeTag]: Id[_ >: IdMix] = int match {
      case _ if typeOf[A] <:< typeOf[String] => Id(int.toString)
      case _ if typeOf[A] <:< typeOf[Long]   => Id(int.toLong)
      case _ if typeOf[A] <:< typeOf[UUID]   => Id(UUID.fromString(int.toString))
    }
  }
}

object IdImplicits extends IdImplicitLike

object Id extends IdImplicitLike {
  def empty[A: TypeTag]: Id[_ >: IdMix] = "" match {
    case _ if typeOf[A] <:< typeOf[String] => IdType.StringWitness.empty
    case _ if typeOf[A] <:< typeOf[Long]   => IdType.LongWitness.empty
    case _ if typeOf[A] <:< typeOf[UUID]   => IdType.UUIDWitness.empty
  }

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
