package edison.yaml.project

import edison.model.domain
import edison.yaml.ScalaYamlLoader
import edison.yaml.project.ProjectDefinitionParser.ParseResult
import org.yaml.snakeyaml.error.YAMLException

import scala.util.{ Failure, Success, Try }

object ProjectDefinitionParser {
  // Unfortunately, Either is not Right-biased so map/flatMap/for-comprehensions do not work with Either and we have to use Try
  type ParseResult[+T] = Try[T]
}

/**
 * Parses the project definition (YAML document).
 *
 * First it converts (using SnakeYAML wrapper) the input string to nested scala collections.
 * Then it recursively parses the nested collections (Any, Map[Any, Any], Seq[Any], etc.) into domain.Project.
 *
 * @see ProjectParser, DocumentStructure
 */
class ProjectDefinitionParser {
  val yamlParser = new ScalaYamlLoader

  def parse(yamlString: String): ParseResult[domain.Project] = {
    val parsedMap = Try { yamlParser.load(yamlString) }

    parsedMap match {
      case Success(topLevel) => ProjectParser.parse(topLevel)
      case Failure(throwable) => throwable match {
        case x: YAMLException => ParserHelpers.parseError(Errors.YamlError, throwable.getMessage)
        case _ => ParserHelpers.parseError(Errors.UnknownError, throwable.getMessage)
      }
    }
  }
}

