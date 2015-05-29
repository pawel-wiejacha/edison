package edison.yaml.project

import edison.model.domain
import edison.yaml.project.ParserHelpers.Mapping
import edison.yaml.project.primitives.{ Derived, Element, Field }

/**
 * This object defines the Edison project definition YAML document structure.
 *
 * It also defines the parser error hierarchy, for example `Structure.Project.SearchDomain.ParamDef.Type.Missing`
 * means that parameter definition is missing a `type: XXX` element.
 */
object DocumentStructure {
  object Project extends Element {
    object ProjectName extends Field[String]("project-name")
    object SearchDomain extends Field[Seq[Any]]("search-domain") {
      object ParamDef extends Element {
        object ParamName extends Field[String]("param-name")
        object ParamDomain extends Field[Mapping]("domain") {
          object Type extends Field[String]("type")
          object ParamDomainInteger extends Derived[domain.ParamDomainInteger] {
            object Start extends Field[Int]("start")
            object End extends Field[Int]("end")
            object Step extends Field[Int]("step")
          }
          object ParamDomainEnum extends Derived[domain.ParamDomainEnum[_]] {
            object Values extends Field[Seq[Any]]("values")
          }
        }
      }
    }
  }
}
