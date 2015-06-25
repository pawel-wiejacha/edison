package edison.model.serialization

import java.util.NoSuchElementException

import edison.model.domain.{ Param, Point, SearchDomain }
import org.json4s.JsonAST._
import org.json4s.jackson.JsonMethods._

import scala.util.Try

object PointParser {

  class PointParserException(msg: String) extends RuntimeException(msg)

  def apply(searchDomain: SearchDomain, pointJson: String): Try[Point] = Try {
    parse(pointJson, useBigDecimalForDouble = false) match {
      case JObject(fields) =>
        val params = fields.map(item => parseParam(searchDomain, item))
        Point(searchDomain, params.toVector)
      case _ => throw new PointParserException("Point should be represented as a mapping")
    }
  }

  private def parseParam(searchDomain: SearchDomain, field: JField): Param = {
    val paramDef = {
      if (searchDomain.paramDefs.map.contains(field._1))
        searchDomain.paramDefs.apply(field._1)
      else
        throw new PointParserException("Invalid parameter name: %s".format(field._1))
    }

    try {
      paramDef.createUnsafe(getParamValue(field._2))
    } catch {
      case _: ClassCastException | _: NoSuchElementException =>
        throw new PointParserException("Invalid parameter value: %s -> %s".format(field._1, field._2))
    }
  }

  private def getParamValue: JValue => Any = {
    case value: JInt => value.num.toInt
    case value: JValue => value.values
  }
}
