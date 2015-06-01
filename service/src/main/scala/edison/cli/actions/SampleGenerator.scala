package edison.cli.actions

import edison.cli.Environment
import edison.search.tree.Helpers.TreePrettyPrinter
import edison.search.tree.builder.TreeBuilder
import org.slf4j.LoggerFactory

/** Generates samples for further evaluation */
trait SampleGenerator {
  def generateSample(env: Environment): Unit
}

object SampleGenerator extends SampleGenerator {
  val logger = LoggerFactory.getLogger("service.cli.SampleGenerator")

  def generateSample(env: Environment): Unit = {
    val treeBuilder = new TreeBuilder
    val tree = treeBuilder.build(env.project.searchDomain)

    logger.info("Created search tree: {}", tree.json)
    logger.info("Generated new sample: {}", tree.generateSample)
  }
}
