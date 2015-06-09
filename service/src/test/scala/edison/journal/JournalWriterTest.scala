package edison.journal

import edison.cli.io.IO
import edison.util.SmartSpec
import org.scalamock.scalatest.MockFactory
import scaldi.Module

class JournalWriterTest extends SmartSpec with MockFactory {
  val ioMock = mock[IO]

  def journalWriter = {
    implicit val injector = new Module { bind[IO] to ioMock }
    new JournalWriter("file/path")
  }

  class SampleEntry extends JournalEntry {
    override def asMapping: Map[String, Any] = {
      Map("foo" -> 5, "bar" -> "baz")
    }
  }

  behavior of "JournalWriter"

  it must "append to the file a serialized entry" in {
    val expectedEntryRepr =
      """
        |# begin
        |foo: 5
        |bar: baz
        |# end
      """.strip
    (ioMock.appendToFile _).expects("file/path", expectedEntryRepr)

    journalWriter.write(new SampleEntry)
  }

}
