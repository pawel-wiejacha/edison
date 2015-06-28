package edison.cli

import edison.cli.actions.{ ResultRecorder, SampleGenerator }
import edison.cli.io.{ DefaultIO, IO }
import edison.journal.JournalWriter
import edison.yaml.project.ProjectDefinitionParser
import scaldi.Module

/** Default DI bindings for Edison CLI */
class CliModule extends Module {
  bind[IO] to DefaultIO
  bind[SampleGenerator] to injected[SampleGenerator]
  bind[ResultRecorder] to injected[ResultRecorder]
  bind[EdisonOptionParser] to new EdisonOptionParser
  bind[ProjectDefinitionParser] to new ProjectDefinitionParser
  bind[JournalWriter] to injected[JournalWriter]
}