package edison.yaml

import edison.util.Union
import org.yaml.snakeyaml.nodes.{ Node, Tag }
import org.yaml.snakeyaml.representer.{ Represent, Representer }
import org.yaml.snakeyaml.{ DumperOptions, Yaml }

import scala.collection.JavaConversions._
import scala.reflect.ClassTag

/**
 * YamlDumper that supports:
 *   - primitive types
 *   - Scala lists, maps and sets
 *     - can be nested
 *   - custom types (manual conversion to Map[String, Any] or to List[Any] has to be provided)
 */
class ScalaYamlDumper(representer: ScalaObjRepresenter, dumperOptions: DumperOptions) extends YamlDumper {
  private val yamlDumper = new Yaml(representer, dumperOptions)

  def dump(data: Any): String = {
    yamlDumper.dump(data)
  }
}

class ScalaObjRepresenter extends Representer {

  type Representation = Union[Map[String, Any], List[Any]]

  multiRepresenters.put(classOf[List[_]], new RepresentList())
  multiRepresenters.put(classOf[Map[_, _]], new RepresentMap())
  multiRepresenters.put(classOf[Set[_]], new RepresentSet())

  /**
   * Custom types can be serialized if conversion to Map/List is provided.
   */
  def addCustomRepresenter[From, To: Representation#Check](converter: From => To)(implicit ev: ClassTag[From]): Unit =
    multiRepresenters.put(ev.runtimeClass, new RepresentType[From, To]({ x: From => converter(x) }))

  private class RepresentList extends Represent {
    def representData(data: Object): Node = {
      val scalaList = data.asInstanceOf[List[_]]
      representSequence(getTag(scalaList.getClass, Tag.SEQ), scalaList, null)
    }
  }

  private class RepresentMap extends Represent {
    def representData(data: Object): Node = {
      val scalaMap = data.asInstanceOf[Map[_, _]]
      representMapping(getTag(scalaMap.getClass, Tag.MAP), scalaMap, null)
    }
  }

  private class RepresentSet extends Represent {
    def representData(data: Object): Node = {
      val scalaMap = data.asInstanceOf[Set[_]].iterator.map(elem => elem -> "").toMap
      representMapping(getTag(scalaMap.getClass, Tag.SET), scalaMap, null)
    }
  }

  class RepresentType[From, To: Representation#Check](converter: From => To) extends Represent {
    def representData(data: Object): Node = {
      converter(data.asInstanceOf[From]) match {
        case typeAsMap: Map[_, _] => representMapping(getTag(typeAsMap.getClass, Tag.MAP), typeAsMap, null)
        case typeAsList: List[_] => representSequence(getTag(typeAsList.getClass, Tag.SEQ), typeAsList, null)
      }
    }
  }
}
