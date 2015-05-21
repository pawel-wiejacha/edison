package edison.cli

object EdisonCli {
  def main(args: Array[String]): Unit = {
    (new EdisonOptionParser).parse(args, Config())
  }
}
