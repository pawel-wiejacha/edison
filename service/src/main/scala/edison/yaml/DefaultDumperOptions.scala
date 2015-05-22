package edison.yaml

import org.yaml.snakeyaml.DumperOptions

class DefaultDumperOptions extends DumperOptions {
  setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
}
