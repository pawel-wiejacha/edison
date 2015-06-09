package edison.cli.io

import scala.util.Try

/**
 * I/O Layer.
 *
 * All files are UTF-8 encoded.
 */
trait IO {
  def readFile(filePath: String): Try[String]
  def appendToFile(filePath: String, content: String): Try[Unit]
}
