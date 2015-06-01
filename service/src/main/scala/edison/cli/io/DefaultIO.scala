package edison.cli.io

import scala.io.Source
import scala.util.Try

object DefaultIO extends IO {
  override def readFile(filePath: String): Try[String] = Try {
    Source.fromFile(filePath).mkString
  }
}
