package edison.model.serialization

import edison.model.domain.SampleData
import edison.model.serialization.PointParser.PointParserException
import edison.search.serialization.JsonSerialization
import edison.util.SmartSpec
import com.fasterxml.jackson.core.JsonParseException

class ModelDeserializationTest extends SmartSpec with SampleData {

  import DefaultSerializers.PointSerializer

  behavior of "ModelDeserialization"

  it should "deserialize previously serialized point" in {
    val pointRepr = JsonSerialization.serializeToString(point1)
    PointParser(searchDomain, pointRepr).get shouldBe point1
  }

  it should "report malformed YAML representation (syntax error)" in {
    PointParser(searchDomain, "{ invalid").failed.get shouldBe a[JsonParseException]
  }

  it should "report malformed YAML representation (not a mapping)" in {
    val exn = intercept[PointParserException] { PointParser(searchDomain, """[ { "a": 3 } ]""").get }
    exn.getMessage shouldBe "Point should be represented as a mapping"
  }

  it should "report malformed YAML representation (invalid parameter name)" in {
    val exn = intercept[PointParserException] { PointParser(searchDomain, """{ "invalid": 3 }""").get }
    exn.getMessage should include("Invalid parameter name")
  }

  it should "report malformed YAML representation (invalid parameter value)" in {
    val exn = intercept[PointParserException] { PointParser(searchDomain, """{ "EvictionPolicy": "bar" }""").get }
    exn.getMessage should include("Invalid parameter value")
  }

  it should "report malformed YAML representation (invalid parameter type)" in {
    val exn = intercept[PointParserException] { PointParser(searchDomain, """{ "CacheSize": "bar" }""").get }
    exn.getMessage should include("Invalid parameter value")
  }

}
