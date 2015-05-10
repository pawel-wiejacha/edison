package edison.search.serialization

import edison.search.{Sample, Samples}
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._

trait SampleSerializer extends JsonSerializer[Sample] {
  override def serialize(sample: Sample): JObject = {
    ("value" -> sample.value.asInt) ~ ("result" -> sample.result)
  }
}

trait SamplesSerializer extends JsonSerializer[Samples] {
  def sampleSerializer: SampleSerializer

  override def serialize(samples: Samples): JObject = {
    val values = samples.values.map({ sample => sampleSerializer.serialize(sample) })

    ("values" -> values) ~
      ("size" -> samples.size) ~
      ("min" -> samples.min) ~
      ("max" -> samples.max) ~
      ("mean" -> samples.mean) ~
      ("sd" -> samples.sd)
  }
}
