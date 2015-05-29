package edison.util

object ClassUtil {
  /**
   * Helper that translates class names to human-readable form, e.g. `package.class$$anon$1$Foo$Bar$` into `Foo.Bar`
   */
  def getScalaClassName(clazz: Class[_], startFrom: Class[_]): String = {
    assert(clazz.toString.startsWith(startFrom.toString), s"$clazz does not include $startFrom")

    // thanks to JDK-8057919 bug (fixed in OpenJDK 9) we cannot use Class.getSimpleName()
    clazz.toString
      .drop(startFrom.toString.length)
      .replaceFirst("\\$$", "")
      .replaceAll("^\\$", "")
      .replaceAll("\\$", ".")
  }

  def getSimpleScalaClassName(clazz: Class[_]): String = {
    clazz.toString.split("\\$").lastOption.getOrElse(clazz.toString)
  }
}
