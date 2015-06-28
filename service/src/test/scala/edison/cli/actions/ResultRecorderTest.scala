package edison.cli.actions

import java.io.IOException

import com.fasterxml.jackson.core.JsonParseException
import edison.cli.{ Config, Environment, StoreResultAction }
import edison.journal.JournalWriter
import edison.model.domain.{ Project, SampleData }
import edison.util.{ NoLogging, SmartFreeSpec }
import org.scalamock.scalatest.MockFactory

import scala.util.{ Failure, Success }

class MockableJournalWriter extends JournalWriter(null) // TODO this has to be fixed in ScalaMock

class ResultRecorderTest extends SmartFreeSpec with MockFactory with SampleData {

  val journalWriterMock = mock[MockableJournalWriter]
  val resultRecorder = new ResultRecorder(journalWriterMock) with NoLogging
  val env = Environment(Config(journalFilePath = "journalPath"), Project("project", searchDomain))

  "ResultRecorder" - {

    "can write to journal serialized point" in {
      val pointJson = """{ "EvictionPolicy": "FIFO", "CacheSize": 5242880 }""" // point1
      val action = StoreResultAction(pointJson, 42.0)

      (journalWriterMock.write _).expects("journalPath", ResultEntry(point1, 42.0)).returning(Success(()))

      resultRecorder.storeResult(action, env).get
    }

    "handles errors gracefully in case of" - {

      "parsing point provided by the user" in {
        val action = StoreResultAction("invalid input", 42.0)
        resultRecorder.storeResult(action, env).failure.exception shouldBe a[JsonParseException]
      }

      "I/O error in JournalWriter" in {
        val action = StoreResultAction("""{ "EvictionPolicy": "FIFO", "CacheSize": 5242880 }""", 42.0)
        (journalWriterMock.write _).expects(*, *).returning(Failure(new IOException()))

        resultRecorder.storeResult(action, env).failure.exception shouldBe a[IOException]
      }
    }
  }
}
