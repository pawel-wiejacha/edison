package edison.search.serialization

import org.json4s.JsonAST.{ JObject, JValue }
import org.json4s.jackson.JsonMethods._

import scala.language.implicitConversions

/** Type class responsible for JSON serialization */
trait JsonSerializer[T] {
  def serialize(obj: T): JObject
}

/**
 * Unfortunately, json4s serialization does not work here because of https://github.com/json4s/json4s/issues/76
 */
object JsonSerialization {

  def serialize[T](obj: T)(implicit serializer: JsonSerializer[T]): JObject = {
    serializer.serialize(obj)
  }

  def serializeToString[T](obj: T)(implicit serializer: JsonSerializer[T]): String = {
    ExtendedJson(serialize(obj)).normalized
  }

  implicit class ExtendedJson(json: JValue) {
    def normalized: String = compact(render(json))
  }

  object DefaultSerializers {
    implicit object SampleSerializer extends SampleSerializer
    implicit object SamplesSerializer extends SamplesSerializer {
      def sampleSerializer = SampleSerializer
    }
  }

}

