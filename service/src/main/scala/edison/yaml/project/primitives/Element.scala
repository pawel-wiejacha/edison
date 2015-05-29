package edison.yaml.project.primitives

import edison.yaml.project.ParserHelpers._
import edison.yaml.project.ProjectDefinitionParser.ParseResult
import edison.yaml.project.StructuralErrorCategory

/**
 * Label of a DocumentStructure element that defines either a top-level element or a sequence element.
 */
abstract class Element extends BaseElement[Any, Mapping] {
  object NotAMapping extends StructuralErrorCategory(this)

  def parse(obj: Any): ParseResult[Mapping] =
    shape[Mapping](obj) orError this.NotAMapping
}
