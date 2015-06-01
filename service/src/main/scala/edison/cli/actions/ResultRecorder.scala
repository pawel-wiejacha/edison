package edison.cli.actions

import edison.cli.{ Environment, StoreResultAction }
import org.slf4j.LoggerFactory

/** Stores evaluated samples in the journal */
trait ResultRecorder {
  def storeResult(action: StoreResultAction, env: Environment): Unit
}

object ResultRecorder extends ResultRecorder {
  val logger = LoggerFactory.getLogger("service.cli.ResultRecorder")

  def storeResult(action: StoreResultAction, env: Environment): Unit = {
    logger.info("Storing result: {} => {}", action.sample, action.result)
  }
}
