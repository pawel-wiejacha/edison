package edison.yaml.project.primitives

import edison.util.ClassUtil
import edison.yaml.project.DocumentStructure

/** Base trait of all DocumentStructure elements. */
trait BaseElement[Source, Result] {
  def parserName = ClassUtil.getScalaClassName(getClass, DocumentStructure.getClass)
}
