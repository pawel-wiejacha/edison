package edison.search.serialization

import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.JsonMethods.render

import scala.language.implicitConversions

/** Type class responsible for JSON serialization */
trait JsonSerializer[-T] {
  def serialize(obj: T): JValue
}

/**
 * Unfortunately, json4s serialization does not work here because of https://github.com/json4s/json4s/issues/76
 */
object JsonSerialization {

  def serialize[T](obj: T)(implicit serializer: JsonSerializer[T]): JValue = {
    serializer.serialize(obj)
  }

  def serializeToString[T](obj: T)(implicit serializer: JsonSerializer[T]): String = {
    ExtendedJson(serialize(obj)).normalized
  }

  implicit class ExtendedJson(json: JValue) {
    def normalized: String = JsonMethods.compact(render(json))
    def pretty: String = JsonMethods.pretty(render(json))
  }

  object DefaultSerializers {

    implicit object SampleSerializer extends SampleSerializer

    implicit object SamplesSerializer extends SamplesSerializer {
      def sampleSerializer = SampleSerializer
    }

    implicit object TreeSerializer extends TreeSerializer {
      def samplesSerializer = SamplesSerializer
    }
  }

}

