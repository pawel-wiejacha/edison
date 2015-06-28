package edison.cli

import edison.search.Result

sealed trait CliAction
case class GenerateSampleAction() extends CliAction
case class StoreResultAction(pointJson: String, result: Result) extends CliAction

case class Config(
  definitionFilePath: String = "",
  journalFilePath: String = "",
  action: CliAction = null
)
