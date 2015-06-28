package edison.yaml

import edison.model.domain.SampleData
import edison.search.serialization.JsonSerialization
import edison.util.SmartSpec
import edison.model.serialization.DefaultSerializers.PointSerializer
import org.json4s.JsonAST.JInt

import scala.collection.Map

class JsonAdapterTest extends SmartSpec with SampleData {

  behavior of "JsonAdapter"

  it must "convert JInt to Integer instead of BigInt" in {
    val value = JsonAdapter.translate(JInt(42))
    value shouldBe 42
    value shouldBe a[Integer]
  }

  it must "allow for reusing JSON serializers" in {
    val serializedPoint = JsonAdapter.translate(JsonSerialization.serialize(point1)).asInstanceOf[Map[String, Any]]
    serializedPoint("CacheSize") shouldBe 5242880
    serializedPoint("CacheSize") shouldBe a[Integer]
    serializedPoint("EvictionPolicy") shouldBe "FIFO"
  }
}
