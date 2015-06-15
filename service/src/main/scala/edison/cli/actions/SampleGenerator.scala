package edison.cli.actions

import com.typesafe.scalalogging.StrictLogging
import edison.cli.Environment
import edison.cli.io.IO
import edison.model.domain.{ Point, SearchDomain }
import edison.model.serialization.DefaultSerializers._
import edison.search.Value
import edison.search.serialization.JsonSerialization
import edison.search.serialization.JsonSerialization.ExtendedJson
import edison.search.tree.Helpers.TreePrettyPrinter
import edison.search.tree.builder.TreeBuilder

/** Generates samples for further evaluation. */
class SampleGenerator(io: IO) extends StrictLogging {
  private val treeBuilder = new TreeBuilder

  def generateSample(env: Environment): Unit = {
    val tree = treeBuilder.build(env.project.searchDomain)
    val point = translateToPoint(env.project.searchDomain, tree.generateSample)
    val serializedPoint = JsonSerialization.serialize(point).pretty

    io.writeToStdout(serializedPoint)

    logger.info("Created search tree: {}", tree.json)
    logger.info("Generated new sample: {}", point)
  }

  private def translateToPoint(searchDomain: SearchDomain, sample: Value): Point = {
    // this is temporary
    val param = searchDomain.paramDefs(0).createUnsafe(sample.asInt)
    val params = Vector(param)

    Point(searchDomain, params)
  }
}
