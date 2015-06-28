package edison.journal

import edison.cli.io.IO
import edison.yaml.{ DefaultDumperOptions, ScalaObjRepresenter, ScalaYamlDumper }

import scala.util.Try

trait JournalEntry {
  def asMapping: Map[String, Any]
}

class JournalEntryRepresenter extends ScalaObjRepresenter {
  addCustomRepresenter({ entry: JournalEntry => entry.asMapping })
}

class JournalEntryYamlDumper extends ScalaYamlDumper(new JournalEntryRepresenter, new DefaultDumperOptions)

class JournalWriter(io: IO) {
  private val dumper = new JournalEntryYamlDumper

  def write(filePath: String, entry: JournalEntry): Try[Unit] = {
    val entryYaml = dumper.dump(entry)
    val entryRepr = "# begin\n%s# end\n".format(entryYaml)
    io.appendToFile(filePath, entryRepr)
  }
}
