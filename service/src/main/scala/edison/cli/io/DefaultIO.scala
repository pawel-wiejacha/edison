package edison.cli.io

import java.io._
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Paths }

import scala.util.Try

object DefaultIO extends IO {
  override def readFile(filePath: String): Try[String] = Try {
    new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8)
  }

  override def appendToFile(filePath: String, content: String): Try[Unit] = Try {
    val out = new OutputStreamWriter(new FileOutputStream(filePath, true), StandardCharsets.UTF_8)
    try
      out.write(content)
    finally
      out.close()
  }
}
