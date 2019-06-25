import java.util.UUID
import model.persistence.Types._
import model.persistence.{Copier, HasId, Id}
import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatestplus.junit.JUnitRunner

/** Top-level case classes for [[Copier]] */
case class X(a: String, id: Int)
case class XOptionLong(a: String, id: IdOptionLong) extends HasId[XOptionLong, OptionLong]
case class XLong(a: String, id: IdLong) extends HasId[XLong, Long]
case class XUuid(a: String, id: IdUuid) extends HasId[XUuid, UUID]
case class XOptionUuid(a: String, id: IdOptionUuid) extends HasId[XOptionUuid, OptionUuid]

@RunWith(classOf[JUnitRunner])
class IdTest extends WordSpec with MustMatchers {
  val longValue = 1L
  val uuidValue: UUID = UUID.randomUUID

  val idLongEmpty: Id[Long] = Id.empty[Long]
  val idLongSome: Id[Long] = Id(longValue)
  val idOptionLongEmpty: Id[OptionLong] = Id.empty[OptionLong]
  val idOptionLongSome: Id[Option[Long]] = Id(Option(longValue))
  val idUuidEmpty: Id[UUID] = Id.empty[UUID]
  val idUuidSome: Id[UUID] = Id(uuidValue)
  val idOptionUuidEmpty: Id[OptionUuid] = Id.empty[OptionUuid]
  val idOptionUuidSome: Id[Option[UUID]] = Id(Option(uuidValue))

  "Copier" should {
    "only works on top-level case classes" in {
      val x = X("hi", 123)
      val desired = x.copy(id=456)
      Copier(x, ("id", 456)) shouldBe desired
    }

    "work for HasId[_, OptionLong]" in {
      val x = XOptionLong("hi", Id(Some(123L)))
      val desired = XOptionLong("hi", Id(Some(456L)))

      val actual0 = Copier(x, ("id", Id[OptionLong](Some(456L))))
      actual0 shouldBe desired

      val actual = x.setId(Id(Some(456L)))
      actual shouldBe desired

      val actual2 = x.setId(Id.empty)
      val desired2 = XOptionLong("hi", Id.empty)
      actual2 shouldBe desired2
    }

    "work for HasId[_, Long]" in {
      val x: XLong = XLong("hi", Id(123L))
      val desired = XLong("hi", Id(456L))

      val actual0 = Copier(x, ("id", Id(456L)))
      actual0 shouldBe desired

      val actual = x.setId(Id(456L))
      actual shouldBe desired

      val actual2 = x.setId(Id.empty)
      val desired2 = XLong("hi", Id.empty)
      actual2 shouldBe desired2
    }

    val uid1 = UUID.randomUUID
    val uid2 = UUID.randomUUID

    "work for HasId[_, UUID]" in {
      val x = XUuid("hi", Id(uid1))
      val desired = XUuid("hi", Id(uid2))

      val actual0 = Copier(x, ("id", Id(uid2)))
      actual0 shouldBe desired

      val actual = x.setId(Id(uid2))
      actual shouldBe desired

      val actual2 = x.setId(Id.empty)
      val desired2 = XUuid("hi", Id.empty)
      actual2 shouldBe desired2
    }

    "work for Id[_, OptionUuid]" in {
      val x = XOptionUuid("hi", Id(Some(uid1)))
      val desired = XOptionUuid("hi", Id[OptionUuid](Some(uid2)))

      val actual0 = Copier(x, ("id", Id[OptionUuid](Some(uid2))))
      actual0 shouldBe desired

      val actual = x.setId(Id(Some(uid2)))
      actual shouldBe desired

      val actual2 = x.setId(Id.empty)
      val desired2 = XOptionUuid("hi", Id.empty)
      actual2 shouldBe desired2
    }
  }

  "Ids" should {
    "provide zero values" in {
      Id.empty[Long].value mustBe 0L
      Id.empty[OptionLong].value mustBe None
      Id.empty[String].value mustBe ""

      val desired: String = new UUID(0L, 0L).toString
      val actual: String = Id.empty[UUID].value.toString
      actual mustBe desired
    }

    "compare to a value" in {
      case class Blah(a: Int, b: String, id: Id[UUID] = Id(UUID.randomUUID))

      val id = Id(UUID.randomUUID)
      val blah = Blah(1, "two", id)
      blah.id mustBe id
    }

    /** The case classes below do not need to be top-level because `setId` is not called,
      * which means that [[Copier]] is not used. */
    "support references" in {
      /** A person can have at most one Dog. Because their Id is based on an Option[UUID],
        * those Ids do not always have a value */
      case class Person(
         age: Int,
         name: String,
         dogId: Id[Option[Long]],
         override val id: IdOptionUuid = Id(Some(UUID.randomUUID))
       ) extends HasId[Person, OptionUuid]

      /** Dogs are territorial. They ensure that no other Dogs are allowed near their FavoriteTrees.
        * Because the Ids for Dog and FavoriteTree are based on Option[Long] not UUID, those Ids might have value None until they are persisted */
      case class Dog(
        species: String,
        color: String,
        override val id: IdOptionLong = Id.empty
      ) extends HasId[Dog, OptionLong]

      /** Dogs can have many Bones. Because a Bone's Id is based on a UUID, they always have a value */
      case class Bone(
         weight: Double,
         dogId: IdOptionLong,
         override val id: IdUuid = Id(UUID.randomUUID)
       ) extends HasId[Bone, UUID]

      /** Many FavoriteTrees for each Dog. Trees can be 'unclaimed', represented by dogId==None */
      case class FavoriteTree(
        diameter: Int,
        latitude: Double,
        longitude: Double,
        dogId: IdOptionLong,
        override val id: IdOptionLong = Id.empty
      ) extends HasId[FavoriteTree, OptionLong]
    }

    "compare to empty / zero" in {
      Id.empty[UUID] mustBe Id(new UUID(0L, 0L))
      Id.empty[String] mustBe Id("")
      Id.empty[Long] mustBe Id(0L)
      Id.empty[OptionLong] mustBe Id[OptionLong](None)

      val idLongZero = Id.empty[Long]
      idLongZero mustBe Id(0L)

      val idOptionLongZero = Id.empty[OptionLong]
      idOptionLongZero mustBe Id[OptionLong](None)

      val idStringZero = Id.empty[String]
      idStringZero mustBe Id("")

      val idUuidZero = Id.empty[UUID]
      idUuidZero mustBe Id(new UUID(0L, 0L))
    }

    "pass validity test" in {
      Id.isValid[UUID](UUID.randomUUID)
      Id.isValid[String]("hello")
      Id.isValid[Long](42L)
      Id.isValid[OptionLong](Some(42L))
    }

    "pass empty / zero test 1" in {
      Id.isEmpty(Id.empty[UUID])
      Id.isEmpty(Id.empty[String])
      Id.isEmpty(Id.empty[Long])
      Id.isEmpty(Id.empty[OptionLong])
    }

    "pass empty / zero test 2" in {
      Id.isEmpty(Id(new UUID(0L, 0L)))
      Id.isEmpty(Id(""))
      Id.isEmpty(Id(0L))
      Id.isEmpty(Id[OptionLong](None))
    }

    "generate toString property" in {
      Id("hi").toString shouldBe "hi"
      Id[OptionString](Some("hi")).toString shouldBe "hi"

      Id(123L).toString shouldBe "123"
      Id[OptionLong](Some(123L)).toString shouldBe "123"

      Id[OptionLong](None).toString shouldBe ""
      Id[OptionString](None).toString shouldBe ""
      Id[OptionUuid](None).toString shouldBe ""

      val uuid = UUID.randomUUID
      Id(uuid).toString shouldBe uuid.toString
      Id[OptionUuid](Some(uuid)).toString shouldBe uuid.toString
    }

    "convert to Option" in {
      idLongEmpty.toOption       shouldBe idOptionLongEmpty
      idLongSome.toOption        shouldBe idOptionLongSome
      idOptionLongEmpty.toOption shouldBe idOptionLongEmpty
      idOptionLongSome.toOption  shouldBe idOptionLongSome
      idUuidEmpty.toOption       shouldBe idOptionUuidEmpty
      idUuidSome.toOption        shouldBe idOptionUuidSome
      idOptionUuidEmpty.toOption shouldBe idOptionUuidEmpty
      idOptionUuidSome.toOption  shouldBe idOptionUuidSome

      /*idLongEmpty.fromOption.asInstanceOf[IdLong]       shouldBe idLongEmpty
      idLongSome.fromOption.asInstanceOf[IdLong]        shouldBe idLongSome
      idOptionLongEmpty.fromOption.asInstanceOf[IdLong] shouldBe idLongEmpty
      idOptionLongSome.fromOption.asInstanceOf[IdLong]  shouldBe idLongSome
      idUuidEmpty.fromOption.asInstanceOf[IdUuid]       shouldBe idUuidEmpty
      idUuidSome.fromOption.asInstanceOf[IdUuid]        shouldBe idUuidSome
      idOptionUuidEmpty.fromOption.asInstanceOf[IdUuid] shouldBe idUuidEmpty
      idOptionUuidSome.fromOption.asInstanceOf[IdUuid]  shouldBe idUuidSome*/
    }
    "convert to BigDecimal" in {
      val bigDecimalZero = BigDecimal(0)
      (idLongEmpty: BigDecimal) shouldBe bigDecimalZero
      (idOptionLongEmpty: BigDecimal) shouldBe bigDecimalZero
      (idLongEmpty: BigDecimal) shouldBe bigDecimalZero
      (idOptionLongEmpty: BigDecimal) shouldBe bigDecimalZero
    }
  }
}
