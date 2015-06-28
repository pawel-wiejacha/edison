package edison.cli.actions

import com.typesafe.scalalogging.StrictLogging
import edison.cli.{ Environment, StoreResultAction }
import edison.journal.{ JournalEntry, JournalWriter }
import edison.model.domain.Point
import edison.model.serialization.DefaultSerializers.PointSerializer
import edison.model.serialization.PointParser
import edison.search.Result
import edison.search.serialization.JsonSerialization
import edison.yaml.JsonAdapter

import scala.util.Try

private[actions] case class ResultEntry(point: Point, result: Result) extends JournalEntry {
  override def asMapping = Map(
    "sample" -> JsonAdapter.translate(JsonSerialization.serialize(point)),
    "result" -> result
  )
}

/** Stores evaluated samples in the journal */
class ResultRecorder(journalWriter: JournalWriter) extends StrictLogging {
  def storeResult(action: StoreResultAction, env: Environment): Try[Unit] = {
    logger.info(s"Storing result: ${action.pointJson}, ${action.result}")

    for {
      point <- PointParser(env.project.searchDomain, action.pointJson)
      writeResult <- journalWriter.write(env.config.journalFilePath, ResultEntry(point, action.result))
    } yield writeResult
  }
}
