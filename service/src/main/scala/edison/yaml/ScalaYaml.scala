package edison.yaml

/**
 * Allows to perform YAML serialization and parsing in a convenient way.
 */
object ScalaYaml extends YamlLoader with YamlDumper {

  override def load(data: String): Any = loader.load(data)
  override def dump(data: Any): String = dumper.dump(data)

  private val loader = new ScalaYamlLoader(new ScalaObjConstructor)
  private val dumper = new ScalaYamlDumper(new ScalaObjRepresenter, new DefaultDumperOptions)
}

