package edison.yaml.project

/** @see Errors */
class ErrorCategory {
  def name: String = {
    // thanks to JDK-8057919 bug (fixed in OpenJDK 9) we cannot use Class.getSimpleName()
    getClass.toString
      .drop(Errors.getClass.toString.length)
      .replaceFirst("\\$$", "")
      .replaceAll("\\$", "::")
  }
}
