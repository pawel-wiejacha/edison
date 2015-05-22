package edison.yaml

/** Parses string containing a YAML document and converts it to a Scala object */
trait YamlLoader {
  def load(data: String): Any
}
