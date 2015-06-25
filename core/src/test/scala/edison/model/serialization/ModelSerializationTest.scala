package edison.model.serialization

import edison.model.domain.SampleData
import edison.search.serialization.{ JsonSerialization, JsonSerializer }
import edison.util.SmartSpec

class ModelSerializationTest extends SmartSpec with SampleData {
  import edison.model.serialization.DefaultSerializers._

  def serialize[T](obj: T)(implicit serializer: JsonSerializer[T]): String =
    JsonSerialization.ExtendedJson(JsonSerialization.serialize(obj)).pretty

  behavior of "ParamSerializer"

  it must "serialize integer Param" in {
    serialize(param1_5mb) shouldBe
      """
        |{
        |  "CacheSize" : 5242880
        |}
      """.strip
  }

  it must "serialize enum Param" in {
    serialize(param0_fifo) shouldBe
      """
        |{
        |  "EvictionPolicy" : "FIFO"
        |}
      """.strip
  }

  behavior of "PointSerializer"

  it must "serialize Point" in {
    serialize(point1) shouldBe
      """
        |{
        |  "EvictionPolicy" : "FIFO",
        |  "CacheSize" : 5242880
        |}
      """.strip
  }

}
