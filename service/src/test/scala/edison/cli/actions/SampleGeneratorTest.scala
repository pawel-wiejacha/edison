package edison.cli.actions

import edison.cli.io.IO
import edison.cli.{ Config, Environment }
import edison.model.domain._
import edison.util.{ NoLogging, SmartSpec }
import org.scalamock.scalatest.MockFactory

class SampleGeneratorTest extends SmartSpec with MockFactory {
  val ioMock = mock[IO]
  val generator = new SampleGenerator(ioMock) with NoLogging

  behavior of "SampleGenerator"

  it must "write sampled point to console" in {
    ioMock.writeToStdout _ expects argThat { msg: String => msg.contains("CacheSize") }

    val cacheSizeParam = ParamDef("CacheSize", ParamDomainInteger(Range.inclusive(1, 100)))
    val searchDomain = SearchDomain(ParamDefs(cacheSizeParam))
    val project = Project("A project", searchDomain)

    generator.generateSample(Environment(stub[Config], project))
  }

}
