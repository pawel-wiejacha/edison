package edison.yaml.project

/**
 * Defines the error category that is both human- and machine-readable.
 *
 * For example: `Structural.Project.ProjectName.Missing`
 */
trait ErrorCategory {
  def errorName: String
}
