package edison.cli

import edison.model.domain.Project

/** Holds environment (parsed config, project definition, journal) for action processing. */
case class Environment(config: Config, project: Project)
