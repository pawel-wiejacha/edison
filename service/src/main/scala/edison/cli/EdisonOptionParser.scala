package edison.cli

import edison.search.IntValue

class EdisonOptionParser extends scopt.OptionParser[Config]("edison") {

  val edisonVersion: String = "0.0.1"

  head("Edison", edisonVersion)
  note("Command line interface for Edison\n")

  help("help") text ("Prints this usage text")
  version("version") text ("Prints the CLI version")
  note("")

  opt[String]('d', "definitionFile") required () action { (x, c) =>
    c.copy(definitionFilePath = x)
  } text ("File with the search domain definition")

  opt[String]('j', "journalFile") required () action { (x, c) =>
    c.copy(journalFilePath = x)
  } text ("Journal that stores previous results and actions")

  note("")
  cmd("sample") action { (x, c) =>
    c.copy(action = GenerateSampleAction())
  } text ("Generates a sample to evaluate")
  note("")

  cmd("store") action { (x, c) =>
    c.copy(action = StoreResultAction(IntValue(0), 0.0))
  } text ("Stores the evaluation result of a sample") children (
    opt[Int]('s', "sample") required () action { (x, c) =>
      c.copy(action = c.action.asInstanceOf[StoreResultAction].copy(sample = IntValue(x)))
    } text ("Sample (in JSON format)"),
    opt[Double]('r', "result") required () action { (x, c) =>
      c.copy(action = c.action.asInstanceOf[StoreResultAction].copy(result = x))
    } text ("Result (in JSON format)")
  )

  checkConfig({ config =>
    if (config.action != null) success else failure("No action provided")
  })
}
