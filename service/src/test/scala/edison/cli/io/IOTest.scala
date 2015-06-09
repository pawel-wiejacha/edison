package edison.cli.io

import java.io.File
import java.nio.file.{ Paths, Files }

import edison.util.SmartSpec

import scala.util.Success

class IOTest extends SmartSpec {
  behavior of "I/O Layer"

  def withTmpFile(body: String => Unit) {
    val tmpFile = File.createTempFile("edison", ".tmp")
    try
      body(tmpFile.getPath)
    finally
      tmpFile.delete()
  }

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

}
