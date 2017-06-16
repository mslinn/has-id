import java.util.UUID
import model.persistence.Id
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import org.scalatest.Matchers._

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

    "compare" in {
      case class Blah(a: Int, b: String, id: Id[UUID]=Id(UUID.randomUUID))

      val id = Id(UUID.randomUUID)
      val blah = Blah(1, "two", id)
      blah.id mustBe id
    }

    "pass validity test" in {
      Id.isValid[UUID](UUID.randomUUID)
      Id.isValid[String]("hello")
      Id.isValid[Long](42L)
    }
  }
}
