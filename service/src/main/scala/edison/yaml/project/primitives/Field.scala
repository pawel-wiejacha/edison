package edison.yaml.project.primitives

import java.util.NoSuchElementException

import edison.yaml.project.ParserHelpers._
import edison.yaml.project.ProjectDefinitionParser.ParseResult
import edison.yaml.project.StructuralErrorCategory

import scala.reflect.runtime.universe.TypeTag

/**
 * Label of a DocumentStructure element that defines a field (object property) in the parent element.
 */
abstract class Field[T](name: String) extends BaseElement[Mapping, T] {
  object Missing extends StructuralErrorCategory(this)
  object Invalid extends StructuralErrorCategory(this)

  def parse(obj: Mapping)(implicit ev: TypeTag[T]): ParseResult[T] =
    getField[T](obj, name) recoverWith {
      case exn: NoSuchElementException => parseError(Missing, exn.getMessage)
      case exn: Throwable => parseError(Invalid, exn.getMessage)
    }

  def apply(obj: Mapping)(implicit ev: TypeTag[T]): T = parse(obj).get
}
