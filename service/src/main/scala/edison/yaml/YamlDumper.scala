package edison.yaml

/** Serializes a Scala object to a YAML string */
trait YamlDumper {
  def dump(data: Any): String
}
