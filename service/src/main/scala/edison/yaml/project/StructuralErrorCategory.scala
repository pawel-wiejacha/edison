package edison.yaml.project

import edison.util.ClassUtil
import edison.yaml.project.primitives.BaseElement

/**
 * Defines hierarchical error categories for situations when provided project definition file contains a correct YAML document
 * but has invalid structure (e.g. search domain parameter has no name defined).
 */
class StructuralErrorCategory(parser: BaseElement[_, _]) extends ErrorCategory {
  override def errorName: String =
    "Structural.%s.%s".format(parser.parserName, ClassUtil.getSimpleScalaClassName(this.getClass))
}
