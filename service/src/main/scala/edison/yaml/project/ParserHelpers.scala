package edison.yaml.project

import edison.yaml.project.ProjectDefinitionParser.ParseResult

import scala.reflect.runtime.universe.{ TypeTag, typeOf }
import scala.util.{ Success, Try, Failure }

/**
 * These helpers are used to convert objects returned by YAML parser (that have no type information available)
 * into typed objects.
 */
object ParserHelpers {
  type Mapping = Map[String, Any]

  def parseError[T](category: ErrorCategory, message: String = ""): Failure[T] = Failure(ParseError(category, message))

  implicit class ResultOrError[T](result: ParseResult[T]) {
    def orError(category: ErrorCategory): Try[T] =
      result recoverWith { case exn: Throwable => parseError(category, exn.getMessage) }
  }

  /** Tries to downcast an object of unknown type to requested type */
  def shape[T](any: Any)(implicit ev: TypeTag[T]): ParseResult[T] = {
    any match {
      case _: String if typeOf[String] =:= typeOf[T] =>
        Success(any.asInstanceOf[T])
      case _: Int | _: Double | _: Boolean if typeOf[String] =:= typeOf[T] =>
        Success(any.toString.asInstanceOf[T])
      case _: Int if typeOf[Int] =:= typeOf[T] =>
        Success(any.asInstanceOf[T])
      case map: Map[_, Any] @unchecked if (typeOf[Mapping] =:= typeOf[T]) && map.keys.forall(_.isInstanceOf[String]) =>
        Success(map.asInstanceOf[T])
      case seq: Seq[Any] @unchecked if typeOf[Seq[Any]] =:= typeOf[T] =>
        Success(seq.asInstanceOf[T])
      case _ =>
        Failure[T](new IllegalArgumentException(s"Unexpected type of `$any`"))
    }
  }

  /**
   * Tries to extract an item from a mapping
   *
   * @param obj map returned by a YAML parser
   * @param fieldName requested item key
   * @tparam T requested type of an item value
   */
  def getField[T](obj: Mapping, fieldName: String)(implicit ev: TypeTag[T]): ParseResult[T] = {
    obj.get(fieldName) match {
      case Some(value) => shape[T](value) recoverWith {
        case _ => Failure[T](new IllegalArgumentException(s"Unexpected type of `$fieldName` field in `$obj`: `$value`"))
      }
      case _ => Failure[T](new NoSuchElementException(s"Field `$fieldName` is missing in `$obj`"))
    }
  }

  /** Converts Seq("abc", "def") into Enumeration(abc, def) */
  def parseEnumeration(seq: Seq[Any]): Enumeration =
    new Enumeration {
      seq.foreach({ x: Any => Value(x.toString) })
    }
}
