package edison.search.serialization

import edison.search.{ IntegerTree, Samples, Tree }
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._

trait TreeSerializer extends JsonSerializer[Tree] {
  def samplesSerializer: JsonSerializer[Samples]

  override def serialize(tree: Tree): JObject = {
    tree match {
      case IntegerTree(children, range, samples) =>
        ("name" -> "[%d;%d)".format(range.start, range.last + 1)) ~
          ("samples" -> samplesSerializer.serialize(samples)) ~
          ("children" -> children.map(serialize))
    }
  }
}

