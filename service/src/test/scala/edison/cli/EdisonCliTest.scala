package edison.cli

import java.io.FileNotFoundException

import edison.cli.actions.{ ResultRecorder, SampleGenerator }
import edison.cli.io.IO
import edison.model.domain._
import edison.search.IntValue
import edison.util.SmartFreeSpec
import edison.yaml.project.ParseError
import org.scalamock.scalatest.MockFactory
import edison.util.IntBytes.IntBytes
import scaldi.Module
import scala.language.postfixOps

import scala.util.{ Failure, Success }

class EdisonCliTest extends SmartFreeSpec with MockFactory {
  val ioMock = mock[IO]
  val sampleGeneratorMock = mock[SampleGenerator]
  val resultRecorderMock = mock[ResultRecorder]

  def cli = {
    implicit val injector = new Module {
      bind[IO] to ioMock
      bind[EdisonOptionParser] to new OptionParserForTests
      bind[SampleGenerator] to sampleGeneratorMock
      bind[ResultRecorder] to resultRecorderMock
    } :: new CliModule
    new EdisonCli
  }

  def createConfig(action: CliAction): Config =
    Config(definitionFilePath = "projectFile", journalFilePath = "journalFile", action = action)

  val sampleProject = Project(
    "cache-tuning",
    SearchDomain(ParamDefs(ParamDef("CacheSize", ParamDomainInteger(Range.inclusive(4 MB, 100 MB, 1 MB)))))
  )

  val sampleProjectDefinitionYaml =
    """
      |project-name: cache-tuning
      |search-domain:
      |  -
      |    param-name: CacheSize
      |    domain:
      |      type: Integer
      |      start: 4194304
      |      end: 104857600
      |      step: 1048576
      |    default-value: 20971520
    """.strip

  "EdisonCli" - {
    "when parsing command line options" - {
      "must handle invalid command line options" in {
        cli.createEnvironment(Array.empty).failed.get.getMessage should include("Failed to parse command line options")
      }
    }

    "when parsing project definition file" - {
      val config = createConfig(GenerateSampleAction())

      "must handle IO errors correctly" in {
        (ioMock.readFile _).expects(config.definitionFilePath).returning(Failure(new FileNotFoundException))
        cli.readProjectDefinitionFile(config).failed.get shouldBe a[FileNotFoundException]
      }

      "must handle YAML parser errors correctly" in {
        (ioMock.readFile _).expects(*).returning(Success("!invalid-yaml"))
        cli.readProjectDefinitionFile(config).failed.get shouldBe a[ParseError]
      }

      "must handle correct project definition" in {
        (ioMock.readFile _).expects(*).returning(Success(sampleProjectDefinitionYaml))
        cli.readProjectDefinitionFile(config).get shouldBe sampleProject
      }
    }

    "must forward actions correctly" - {
      "when sample generation is requested" in {
        val env = Environment(createConfig(GenerateSampleAction()), sampleProject)
        (sampleGeneratorMock.generateSample _).expects(env)
        cli.executeAction(env)

      }
      "when storing result is requested" in {
        val action = StoreResultAction(IntValue(123), 456.0)
        val env = Environment(createConfig(action), sampleProject)
        (resultRecorderMock.storeResult _).expects(action, env)
        cli.executeAction(env)
      }
    }
  }

}
