package edison.yaml.project.primitives

import edison.yaml.project.ParserHelpers._

/**
 * Label for a DocumentStructure element that defines a subtype of its parent DocumentStructure element.
 */
abstract class Derived[T] extends BaseElement[Mapping, T]
