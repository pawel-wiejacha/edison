package edison.journal

import edison.cli.io.IO
import edison.yaml.{ DefaultDumperOptions, ScalaObjRepresenter, ScalaYamlDumper }
import scaldi.Injectable.inject
import scaldi.Injector

import scala.util.Try

trait JournalEntry {
  def asMapping: Map[String, Any]
}

class JournalEntryRepresenter extends ScalaObjRepresenter {
  addCustomRepresenter({ entry: JournalEntry => entry.asMapping })
}

class JournalEntryYamlDumper extends ScalaYamlDumper(new JournalEntryRepresenter, new DefaultDumperOptions)

class JournalWriter(filePath: String)(implicit inj: Injector) {
  val dumper = new JournalEntryYamlDumper
  val io = inject[IO]

  def write(entry: JournalEntry): Try[Unit] = {
    val entryYaml = dumper.dump(entry)
    val entryRepr = "# begin\n%s# end".format(entryYaml)
    io.appendToFile(filePath, entryRepr)
  }
}
