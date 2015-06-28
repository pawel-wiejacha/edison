package edison.model.serialization

import edison.model.domain._
import edison.search.serialization.JsonSerializer
import org.json4s.JsonAST.{ JField, JObject, JValue }
import org.json4s.JsonDSL._

trait ParamSerializer extends JsonSerializer[Param] {
  override def serialize(param: Param): JValue = serializeAsField(param)

  def serializeAsField(param: Param): JField =
    param.name -> serializeValue(param.value)

  private def serializeValue: ParamValue => JValue = {
    case IntegerParam(value, _) => value
    case EnumParam(value, _) => value.toString
  }
}

trait PointSerializer extends JsonSerializer[Point] {
  def paramSerializer: ParamSerializer

  override def serialize(point: Point): JObject =
    JObject(point.params.map(paramSerializer.serializeAsField).toList)
}

object DefaultSerializers {
  implicit object ParamSerializer extends ParamSerializer

  implicit object PointSerializer extends PointSerializer {
    override def paramSerializer = ParamSerializer
  }
}
