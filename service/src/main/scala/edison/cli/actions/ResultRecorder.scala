package edison.cli.actions

import com.typesafe.scalalogging.StrictLogging
import edison.cli.{ Environment, StoreResultAction }

/** Stores evaluated samples in the journal */
trait ResultRecorder {
  def storeResult(action: StoreResultAction, env: Environment): Unit
}

object ResultRecorder extends ResultRecorder with StrictLogging {
  def storeResult(action: StoreResultAction, env: Environment): Unit = {
    logger.info(s"Storing result: ${action.sample}, ${action.result}")
  }
}
