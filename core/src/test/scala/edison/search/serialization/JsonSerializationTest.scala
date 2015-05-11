package edison.search

import edison.search.IntValueImplicits.intToIntValue
import edison.search.SampleImplicits.pairToSample
import edison.search.serialization.JsonSerialization.DefaultSerializers._
import edison.search.serialization.{ JsonSerialization, JsonSerializer }
import edison.search.tree.IntegerTree
import edison.util.SmartSpec

class JsonSerializationTest extends SmartSpec {

  def serialize[T](obj: T)(implicit serializer: JsonSerializer[T]): String =
    JsonSerialization.serializeToString(obj)

  def jsonRepr(str: String) = str.stripMargin.replaceAll("[\n ]", "")

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

    serialize(samples) shouldBe jsonRepr(
      """
        |{
        |  "values": [
        |    {"value": 1, "result": 10.0},
        |    {"value": 2, "result": 20.0}
        |  ],
        |  "size": 2,
        |  "min": 10.0,
        |  "max": 20.0,
        |  "mean": 15.0,
        |  "sd": 5.0
        |}
        |""")
  }

  it must "be able to serialize an IntegerTree leaf" in {
    val samples = Samples(1 -> 10.0, 2 -> 20.0)
    val tree = IntegerTree(List.empty, Range(1, 50), samples)

    serialize(tree) shouldBe jsonRepr(
      s"""
        |{
        |  "name": "[1; 50)",
        |  "samples": ${serialize(samples)},
        |  "children": []
        |}
      """)
  }

  it must "be able to serialize an IntegerTree node with children" in {
    val leafA = IntegerTree(List.empty, Range(1, 2), Samples(1 -> 10.0))
    val leafB = IntegerTree(List.empty, Range(2, 3), Samples(2 -> 20.0))
    val root = IntegerTree(List(leafA, leafB), Range(1, 3), Samples(1 -> 10.0, 2 -> 20.0))

    serialize(root) shouldBe jsonRepr(
      s"""
         |{
         |  "name": "[1; 3)",
         |  "samples": ${serialize(root.samples)},
         |  "children": [${serialize(leafA)}, ${serialize(leafB)}]
         |}
      """)
  }

}