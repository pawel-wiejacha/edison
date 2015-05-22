package edison.yaml

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
 *   - custom types (manual conversion to Map[String, Any] has to be provided)
 */
class ScalaYamlDumper(representer: ScalaObjRepresenter, dumperOptions: DumperOptions) extends YamlDumper {
  private val yamlDumper = new Yaml(representer, dumperOptions)

  def dump(data: Any): String = {
    yamlDumper.dump(data)
  }
}

class ScalaObjRepresenter extends Representer {

  multiRepresenters.put(classOf[List[_]], new RepresentList())
  multiRepresenters.put(classOf[Map[_, _]], new RepresentMap())
  multiRepresenters.put(classOf[Set[_]], new RepresentSet())

  def addCustomRepresenter[T](converter: T => Map[String, Any])(implicit ev: ClassTag[T]) = {
    multiRepresenters.put(ev.runtimeClass, new RepresentTypeAsMap[T](converter))
  }

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

  class RepresentTypeAsMap[T](converter: T => Map[String, Any]) extends Represent {
    def representData(data: Object): Node = {
      val personAsMap = converter(data.asInstanceOf[T])
      representMapping(getTag(personAsMap.getClass, Tag.MAP), personAsMap, null)
    }
  }
}
