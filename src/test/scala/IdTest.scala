import java.util.UUID
import model.persistence.{HasId, Id}
import model.persistence.Id.IdMix
import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import scala.language.existentials

@RunWith(classOf[JUnitRunner])
class IdTest extends WordSpec with MustMatchers {
  "Ids" should {
    "provide zero values" in {
      Id.empty[Long].value mustBe 0L
      Id.empty[String].value mustBe ""

      val desired: String = new UUID(0L, 0L).toString
      val actual: String = Id.empty[UUID].value.toString
      actual mustBe desired
    }

    "compare to a value" in {
      case class Blah(a: Int, b: String, id: Id[UUID]=Id(UUID.randomUUID))

      val id = Id(UUID.randomUUID)
      val blah = Blah(1, "two", id)
      blah.id mustBe id
    }

    "compare to zero" in {
      Id.empty[UUID] mustBe Id(new UUID(0L, 0L))
      Id.empty[String] mustBe Id("")
      Id.empty[Long] mustBe Id(0L)

      case class BlahString(a: Int, b: String, override val id: Id[String] = Id.empty[String]) extends HasId
      case class BlahLong(a: Int,   b: String, override val id: Id[Long]   = Id.empty[Long])   extends HasId
      case class BlahUuid(a: Int,   b: String, override val id: Id[UUID]   = Id.empty[UUID])   extends HasId

      val idLongZero: Id[_ >: IdMix] = Id.empty[Long]
      idLongZero mustBe Id(0L)

      val idStringZero: Id[_ >: IdMix] = Id.empty[String]
      idStringZero mustBe Id("")

      val idUuidZero: Id[_ >: IdMix] = Id.empty[UUID]
      idUuidZero mustBe Id(new UUID(0L, 0L))
    }

    "pass validity test" in {
      Id.isValid[UUID](UUID.randomUUID)
      Id.isValid[String]("hello")
      Id.isValid[Long](42L)
    }
  }
}
