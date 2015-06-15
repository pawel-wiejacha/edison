package edison.cli

import edison.cli.actions.{ ResultRecorder, SampleGenerator }
import edison.cli.io.{ DefaultIO, IO }
import scaldi.Module

/** Default DI bindings for Edison CLI */
class CliModule extends Module {
  bind[IO] to DefaultIO
  bind[SampleGenerator] to injected[SampleGenerator]
  bind[ResultRecorder] to ResultRecorder
  bind[EdisonOptionParser] to new EdisonOptionParser
}