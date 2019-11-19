package eisner

import java.lang.reflect.{Constructor, Field, Method}

object Accessibility {
  implicit final class ConstructorOps(private val c: Constructor[_]) extends AnyVal {
    final def initialize: AnyRef = {
      c.setAccessible(true)
      c.newInstance().asInstanceOf[AnyRef]
    }
  }
  implicit final class FieldOps(private val f: Field) extends AnyVal {
    final def inside(i: AnyRef): AnyRef = {
      f.setAccessible(true)
      f.get(i)
    }
  }
  implicit final class MethodOps(private val m: Method) extends AnyVal {
    final def inside(i: AnyRef): AnyRef = {
      m.setAccessible(true)
      m.invoke(i)
    }
  }
}
