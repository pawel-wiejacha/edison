package edison.yaml

import edison.model.domain.SampleData
import edison.search.serialization.JsonSerialization
import edison.util.SmartSpec
import edison.model.serialization.DefaultSerializers.PointSerializer

import scala.collection.Map

class JsonAdapterTest extends SmartSpec with SampleData {

  behavior of "JsonAdapter"

  it must "allow for reusing JSON serializers" in {
    val serializedPoint = JsonAdapter.translate(JsonSerialization.serialize(point1)).asInstanceOf[Map[String, Any]]
    serializedPoint("CacheSize") shouldBe 5242880
    serializedPoint("EvictionPolicy") shouldBe "FIFO"
  }
}
