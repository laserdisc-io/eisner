package eisner

import scala.collection.GenTraversable

sealed trait Writer[A] {
  def write(a: A, tabs: Int): List[String]
}

object Writer {
  @inline final def apply[A](implicit ev: Writer[A]): Writer[A] = ev
  final def instance[A](f: ((A, Int)) => List[String]): Writer[A] = new Writer[A] {
    override final def write(a: A, tabs: Int): List[String] = f(a -> tabs)
  }
  final def instancePF[A](pf: PartialFunction[(A, Int), List[String]]): Writer[A] = new Writer[A] {
    override final def write(a: A, tabs: Int): List[String] = pf.applyOrElse(a -> tabs, (_: (A, Int)) => List.empty)
  }

  implicit final val stringWriter: Writer[String] = Writer.instance {
    case (s, i) => s"${i.tabs}$s" :: Nil
  }
  implicit final def genTraversableWriter[CC[x] <: GenTraversable[x], A](implicit A: Writer[A]): Writer[CC[A]] = Writer.instance {
    case (as, i) => as.toList.flatMap(A.write(_, i))
  }
}
