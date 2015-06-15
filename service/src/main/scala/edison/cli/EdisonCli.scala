package edison.cli

import com.typesafe.scalalogging.StrictLogging
import edison.cli.actions.{ ResultRecorder, SampleGenerator }
import edison.cli.io.IO
import edison.model.domain.Project
import edison.yaml.project.ProjectDefinitionParser
import scaldi.{ Injectable, Injector }

import scala.util.{ Failure, Success, Try }

object CliMain {
  def main(args: Array[String]): Unit = {
    implicit val cliModule = new CliModule
    val cli = new EdisonCli
    cli.main(args)
  }
}

/** Edison command line interface */
class EdisonCli(implicit val injector: Injector) extends Injectable with StrictLogging {
  val io = inject[IO]
  val sampleGenerator = inject[SampleGenerator]
  val resultRecorder = inject[ResultRecorder]
  val optionParser = inject[EdisonOptionParser]

  def main(args: Array[String]): Unit = {
    createEnvironment(args) match {
      case Success(env) => executeAction(env)
      case Failure(exn) =>
        logger.error("Failed to start Edison.", exn)
        System.exit(1)
    }
  }

  def executeAction(env: Environment): Unit = {
    env.config.action match {
      case GenerateSampleAction() => sampleGenerator.generateSample(env)
      case action: StoreResultAction => resultRecorder.storeResult(action, env)
    }
  }

  def createEnvironment(args: Array[String]): Try[Environment] = {
    for {
      config <- optionParser.parse(args)
      project <- readProjectDefinitionFile(config)
    } yield Environment(config, project)
  }

  def readProjectDefinitionFile(config: Config): Try[Project] = {
    val projectParser = new ProjectDefinitionParser
    for {
      definitionYaml <- io.readFile(config.definitionFilePath)
      project <- projectParser.parse(definitionYaml)
    } yield project
  }
}
