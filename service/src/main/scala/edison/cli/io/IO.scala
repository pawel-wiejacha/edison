package edison.cli.io

import scala.util.Try

trait IO {
  def readFile(filePath: String): Try[String]
}
