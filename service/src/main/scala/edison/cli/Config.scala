package edison.cli

import edison.search.{ Result, Value }

sealed trait CliAction
case class GenerateSampleAction() extends CliAction
case class StoreResultAction(sample: Value, result: Result) extends CliAction

case class Config(
  definitionFilePath: String = "",
  journalFilePath: String = "",
  action: CliAction = null
)
