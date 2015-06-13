package edison.model.serialization

import edison.model.domain._
import edison.search.serialization.JsonSerializer
import org.json4s.JsonAST.{ JObject, JValue }
import org.json4s.JsonDSL._

trait ParamSerializer extends JsonSerializer[Param] {
  override def serialize(param: Param): JObject =
    ("name" -> param.name) ~ ("value" -> serializeValue(param.value))

  private def serializeValue(value: ParamValue): JValue =
    value match {
      case IntegerParam(value, _) => value
      case EnumParam(value, _) => value.toString
    }
}

trait PointSerializer extends JsonSerializer[Point] {
  def paramSerializer: JsonSerializer[Param]

  override def serialize(point: Point): JObject =
    "params" -> point.params.map(paramSerializer.serialize)
}

object DefaultSerializers {
  implicit object ParamSerializer extends ParamSerializer

  implicit object PointSerializer extends PointSerializer {
    override def paramSerializer = ParamSerializer
  }
}
