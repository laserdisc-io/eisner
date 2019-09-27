package object eisner {
  implicit final class IntTabs(private val i: Int) extends AnyVal {
    final def tabs: String = "\t" * i
  }
}