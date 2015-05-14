package edison.search.serialization

import edison.search.Samples
import edison.search.tree.{ IntegerTree, Tree }
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._

trait TreeSerializer extends JsonSerializer[Tree] {
  def samplesSerializer: JsonSerializer[Samples]

  override def serialize(tree: Tree): JObject = {
    tree match {
      case IntegerTree(range, children, samples) =>
        val rangeEndChar = if (range.isInclusive) ']' else ')'

        ("name" -> "[%d;%d%c".format(range.start, range.end, rangeEndChar)) ~
          ("samples" -> samplesSerializer.serialize(samples)) ~
          ("children" -> children.map(serialize))
    }
  }
}

