package edison.yaml

import org.json4s.JsonAST.{ JValue, JObject }

/**
 * This adapter allows for reusing JSON serializers to serialize objects to YAML.
 */
object JsonAdapter {
  def translate: JValue => Any = {
    case jObj: JObject => jObj.obj.toMap.mapValues(translate)
    case jValue: JValue => jValue.values
  }
}
