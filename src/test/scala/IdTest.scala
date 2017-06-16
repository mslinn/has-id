import java.util.UUID
import model.persistence.{HasId, Id}
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
      Id.empty[Option[Long]].value mustBe None
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

    "compare to empty / zero" in {
      Id.empty[UUID] mustBe Id(new UUID(0L, 0L))
      Id.empty[String] mustBe Id("")
      Id.empty[Long] mustBe Id(0L)
      Id.empty[Option[Long]] mustBe Id[Option[Long]](None)

      case class BlahString(a: Int,     b: String, id: Id[String] = Id.empty ) extends HasId[String]
      case class BlahLong(a: Int,       b: String, id: Id[Long] = Id.empty) extends HasId[Long]
      case class BlahOptionLong(a: Int, b: String, id: Id[Option[Long]] = Id.empty) extends HasId[Option[Long]]
      case class BlahUuid(a: Int,       b: String, id: Id[UUID] = Id.empty) extends HasId[UUID]

      val idLongZero = Id.empty[Long]
      idLongZero mustBe Id(0L)

      val idOptionLongZero = Id.empty[Option[Long]]
      idOptionLongZero mustBe Id[Option[Long]](None)

      val idStringZero = Id.empty[String]
      idStringZero mustBe Id("")

      val idUuidZero = Id.empty[UUID]
      idUuidZero mustBe Id(new UUID(0L, 0L))

      BlahString( 5, "test" ).copy(
        id = Id("some id")
      )
    }

    "pass validity test" in {
      Id.isValid[UUID](UUID.randomUUID)
      Id.isValid[String]("hello")
      Id.isValid[Long](42L)
      Id.isValid[Option[Long]](Some(42L))
    }

    "pass empty / zero test 1" in {
      Id.isEmpty(Id.empty[UUID])
      Id.isEmpty(Id.empty[String])
      Id.isEmpty(Id.empty[Long])
      Id.isEmpty(Id.empty[Option[Long]])
    }

    "pass empty / zero test 2" in {
      Id.isEmpty(Id(new UUID(0L, 0L)))
      Id.isEmpty(Id(""))
      Id.isEmpty(Id(0L))
      Id.isEmpty(Id[Option[Long]](None))
    }
  }
}
