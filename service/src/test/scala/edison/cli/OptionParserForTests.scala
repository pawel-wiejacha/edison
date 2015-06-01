package edison.cli

class OptionParserForTests extends EdisonOptionParser {
  var errors: String = ""

  override def reportError(msg: String): Unit = errors += msg + "\n"
  override def reportWarning(msg: String): Unit = failure("warnings are not expected in this test")
  override def showTryHelp: Unit = ()
}
