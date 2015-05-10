package edison.search

import edison.search.IntValueImplicits.intToIntValue
import edison.search.SampleImplicits.pairToSample
import edison.search.serialization.JsonSerialization.DefaultSerializers._
import edison.search.serialization.{ JsonSerialization, JsonSerializer }
import edison.util.SmartSpec

class JsonSerializationTest extends SmartSpec {

  def serialize[T](obj: T)(implicit serializer: JsonSerializer[T]): String =
    JsonSerialization.serializeToString(obj)

  behavior of "JSON serialization"

  it must "handle samples with floating point results correctly" in {
    val sample = Sample(1, 0.0625) // 1/16
    serialize(sample) shouldBe """{"value":1,"result":0.0625}"""
  }

  it must "be able to serialize an empty Samples" in {
    serialize(Samples.empty) shouldBe """{"values":[],"size":0}"""
  }

  it must "be able to serialize two Samples" in {
    val samples = Samples(1 -> 10.0, 2 -> 20.0)

    serialize(samples) shouldBe
      """{"values":[
        |{"value": 1, "result": 10.0},
        |{"value": 2, "result": 20.0}
        |],
        |"size":2,
        |"min": 10.0,
        |"max": 20.0,
        |"mean": 15.0,
        |"sd": 5.0}""".stripMargin.replaceAll("[\n ]", "")
  }

}