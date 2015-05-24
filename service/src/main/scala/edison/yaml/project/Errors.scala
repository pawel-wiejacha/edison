package edison.yaml.project

/** Defines the ProjectDefinitionParser error hierarchy */
object Errors {
  object IO extends ErrorCategory
  object Yaml extends ErrorCategory
  object Unknown extends ErrorCategory

  object Semantic {
    object TopLevelNotMap extends ErrorCategory

    object TopLevel {
      object MissingProjectName extends ErrorCategory
      object MissingSearchDomain extends ErrorCategory

      object SearchDomain {
        object ParamDefsNotMap extends ErrorCategory

        object ParamDefs {
          object MissingName extends ErrorCategory
          object MissingType extends ErrorCategory
        }
      }
    }
  }
}
