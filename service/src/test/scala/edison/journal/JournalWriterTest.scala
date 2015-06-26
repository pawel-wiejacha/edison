package edison.journal

import edison.cli.io.IO
import edison.util.SmartSpec
import org.scalamock.scalatest.MockFactory

class JournalWriterTest extends SmartSpec with MockFactory {
  val ioMock = mock[IO]
  val journalWriter = new JournalWriter("file/path", ioMock)

  class SampleEntry extends JournalEntry {
    override def asMapping = Map("foo" -> 5, "bar" -> "baz")
  }

  behavior of "JournalWriter"

  it must "append to the file a serialized entry" in {
    val expectedEntryRepr =
      """
        |# begin
        |foo: 5
        |bar: baz
        |# end
      """.strip + "\n"
    (ioMock.appendToFile _).expects("file/path", expectedEntryRepr)

    journalWriter.write(new SampleEntry)
  }

}
