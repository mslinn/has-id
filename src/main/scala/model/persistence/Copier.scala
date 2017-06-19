package model.persistence

/** Modified from [[https://stackoverflow.com/a/23644859/553865 StackOverflow]] */
object Copier {
  def apply[T](o: T, vals: (String, Any)*): T = {
    val copier = new Copier(o.getClass)
    copier.apply(o, vals: _*)
  }
}

/** Utility class for providing copying of a designated case class with minimal overhead.
  * ONLY WORKS WITH TOP-LEVEL CASE CLASSES */
class Copier(cls: Class[_]) {
  import java.lang.reflect.{Constructor, Method, Modifier}

  private val ctor: Constructor[_] = cls.getConstructors.apply(0)
  private val getters: Array[Method] = cls.getDeclaredFields
    .filter { f =>
      val m: Int = f.getModifiers
      Modifier.isPrivate(m) && Modifier.isFinal(m) && !Modifier.isStatic(m)
    }
    .take(ctor.getParameterTypes.length)
    .map(f => cls.getMethod(f.getName))

  /** A reflective case class copier */
  def apply[T](o: T, vals: (String, Any)*): T = {
    val byIx: Map[Int, Object] = vals.map {
      case (name, value) =>
        val ix: Int = getters.indexWhere(_.getName == name)
        if (ix < 0) throw new IllegalArgumentException(s"Unknown field $name in ${ cls.getName }")
        (ix, value.asInstanceOf[Object])
    }.toMap

    val args: IndexedSeq[AnyRef] = getters.indices.map { i =>
      byIx.getOrElse(i, getters(i).invoke(o))
    }
    try {
      ctor.newInstance(args: _*).asInstanceOf[T]
    } catch {
      case e: Exception =>
        println(s"${ e.getMessage }: ${ ctor.getName }(${ args.mkString(", ") })")
        throw e
    }
  }
}
