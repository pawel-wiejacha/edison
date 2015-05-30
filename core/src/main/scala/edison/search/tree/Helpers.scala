package edison.search.tree

import edison.search.Samples
import edison.search.serialization.{ JsonSerialization, JsonSerializer, TreeSerializer }
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._

object Helpers {

  /** Creates human-readable tree representations */
  implicit class TreePrettyPrinter(tree: Tree) {

    def json: String = {
      import JsonSerialization.DefaultSerializers._
      JsonSerialization.ExtendedJson(JsonSerialization.serialize(tree)).pretty
    }

    /** Tree representation that does not include samples */
    def shortJson: String = {
      implicit object TreeSerializerNoSamples extends TreeSerializer {
        override def samplesSerializer = new JsonSerializer[Samples] {
          override def serialize(obj: Samples): JObject =
            ("size" -> obj.size) ~ ("mean" -> obj.mean)
        }
      }

      JsonSerialization.ExtendedJson(JsonSerialization.serialize(tree)).pretty
    }
  }
}
