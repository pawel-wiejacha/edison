package edison.yaml

import org.yaml.snakeyaml.constructor.{ AbstractConstruct, Constructor }
import org.yaml.snakeyaml.nodes.{ MappingNode, Node, SequenceNode, Tag }
import scala.collection.JavaConversions.asScalaIterator

/**
 * YamlLoader that supports:
 *  - primitive types
 *  - Scala lists, maps and sets
 *    - can be nested, but cannot be self-recursive
 *  - repeated elements
 */
class ScalaYamlLoader(constructor: ScalaObjConstructor) extends YamlLoader {
  private val yamlLoader = new org.yaml.snakeyaml.Yaml(constructor)

  def load(data: String): Any = {
    yamlLoader.load(data)
  }
}

class ScalaObjConstructor extends Constructor {
  yamlConstructors.put(Tag.SEQ, new ConstructSeq)
  yamlConstructors.put(Tag.MAP, new ConstructMap)
  yamlConstructors.put(Tag.SET, new ConstructSet)

  class ConstructSeq extends AbstractConstruct {
    override def construct(node: Node): Seq[Any] = {
      assert(node.isInstanceOf[SequenceNode])

      val seqNode = node.asInstanceOf[SequenceNode]
      seqNode.getValue.iterator.map(elem => constructObject(elem)).toVector
    }
  }

  class ConstructMap extends AbstractConstruct {
    override def construct(node: Node): Map[Any, Any] = {
      assert(node.isInstanceOf[MappingNode])

      val mappingNode = node.asInstanceOf[MappingNode]
      mappingNode.getValue.iterator.map({ nodeTuple =>
        constructObject(nodeTuple.getKeyNode) -> constructObject(nodeTuple.getValueNode)
      }).toMap
    }
  }

  class ConstructSet extends AbstractConstruct {
    override def construct(node: Node): Set[Any] = {
      assert(node.isInstanceOf[MappingNode])

      val mappingNode = node.asInstanceOf[MappingNode]
      mappingNode.getValue.iterator.map({ nodeTuple =>
        constructObject(nodeTuple.getKeyNode)
      }).toSet
    }
  }

}
