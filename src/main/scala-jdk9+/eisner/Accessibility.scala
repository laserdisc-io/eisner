package eisner

import java.lang.reflect.{Constructor, Field, Method}

object Accessibility {
  implicit final class ConstructorOps(private val c: Constructor[_]) extends AnyVal {
    final def initialize: AnyRef = {
      if (!c.canAccess(null)) c.trySetAccessible()
      c.newInstance().asInstanceOf[AnyRef]
    }
  }
  implicit final class FieldOps(private val f: Field) extends AnyVal {
    final def inside(i: AnyRef): AnyRef = {
      if (!f.canAccess(i)) f.trySetAccessible()
      f.get(i)
    }
  }
  implicit final class MethodOps(private val m: Method) extends AnyVal {
    final def inside(i: AnyRef): AnyRef = {
      if (!m.canAccess(i)) m.trySetAccessible()
      m.invoke(i)
    }
  }
}
