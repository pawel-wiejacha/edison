package edison.cli.io

import java.io.{ File, OutputStream, PrintStream }
import java.nio.file.{ Files, Paths }

import edison.util.SmartSpec
import org.scalamock.scalatest.MockFactory

import scala.util.Success

class IOTest extends SmartSpec with MockFactory {

  def withTmpFile(body: String => Unit) {
    val tmpFile = File.createTempFile("edison", ".tmp")
    try
      body(tmpFile.getPath)
    finally
      tmpFile.delete()
  }

  def withMockedStdout(body: PrintStream => Unit) {
    class MockablePrintStream extends PrintStream(mock[OutputStream], false)
    val stdoutMock = mock[MockablePrintStream]

    val originalStdout = System.out
    System.setOut(stdoutMock)

    try
      body(stdoutMock)
    finally
      System.setOut(originalStdout)
  }

  behavior of "I/O Layer"

  it must "be able to read what it wrote" in withTmpFile { filePath =>
    DefaultIO.appendToFile(filePath, "some text") shouldBe Success(())
    DefaultIO.readFile(filePath).get shouldBe "some text"
  }

  it must "use UTF-8 encoding" in withTmpFile { filePath =>
    DefaultIO.appendToFile(filePath, "łoś") shouldBe Success(())
    DefaultIO.readFile(filePath).get shouldBe "łoś"
    Files.readAllBytes(Paths.get(filePath)) shouldBe Array(0xc5 - 256, 0x82 - 256, 'o', 0xc5 - 256, 0x9b - 256)
  }

  it can "append to a file" in withTmpFile { filePath =>
    DefaultIO.appendToFile(filePath, "some text\n") shouldBe Success(())
    DefaultIO.appendToFile(filePath, "some other text") shouldBe Success(())
    DefaultIO.readFile(filePath).get shouldBe "some text\nsome other text"
  }

  it can "write to console" in withMockedStdout { stdoutMock =>
    (stdoutMock.write: (Array[Byte], Int, Int) => Unit).expects(
      argThat { arg: Array[Byte] => arg.take(4).toList == "foo\n".getBytes("UTF-8").toList },
      0, 4
    )
    DefaultIO.writeToStdout("foo")
  }

}