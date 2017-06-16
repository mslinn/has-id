package model.persistence

import java.util.UUID
import com.micronautics.HasValue
import scala.reflect.runtime.universe._

protected sealed trait IdType[+T]

protected object IdType {
  implicit object LongWitness extends IdType[Long]

  implicit object OptionLongWitness extends IdType[Option[Long]]

  implicit object StringWitness extends IdType[String]

  implicit object UUIDWitness extends IdType[UUID]
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

  def empty[A: IdType]: Id[_ >: IdMix] = "" match {
    case _ if typeOf[A] <:< typeOf[String]       => Id("")
    case _ if typeOf[A] <:< typeOf[Long]         => Id(0L)
    case _ if typeOf[A] <:< typeOf[Option[Long]] => Id(None)
    case _ if typeOf[A] <:< typeOf[UUID]         => Id(new UUID(0L, 0L))
  }

  def isValid[T: IdType](value: T): Boolean = try {
    Id(value)
    true
  } catch {
    case _: Exception => false
  }
}

case class Id[T: IdType](value: T) extends HasValue[T] {
  def isEmpty[A: TypeTag]: Boolean = value match {
    case _ if typeOf[A] <:< typeOf[String]       => value == Id.empty[String]
    case _ if typeOf[A] <:< typeOf[Long]         => value == Id.empty[Long]
    case _ if typeOf[A] <:< typeOf[Option[Long]] => value == Id.empty[Option[Long]]
    case _ if typeOf[A] <:< typeOf[UUID]         => value == Id.empty[UUID]
  }

  override def toString: String = value.toString
}

trait HasId extends IdImplicitLike {
  import Id.IdMix

//  def id[U: TypeTag]: Id[_ >: IdMix] = Id.empty[U]
  def id[T >: IdMix]: Id[_] = Id.empty[T]
}
