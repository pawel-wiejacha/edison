package edison.yaml.project

import edison.model.domain
import edison.yaml.project.DocumentStructure.Project
import edison.yaml.project.DocumentStructure.Project.SearchDomain.ParamDef
import edison.yaml.project.DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainEnum.Values
import edison.yaml.project.DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.Type
import edison.yaml.project.DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainInteger.{ End, Start, Step }
import edison.yaml.project.DocumentStructure.Project.SearchDomain.ParamDef.{ ParamDomain, ParamName }
import edison.yaml.project.DocumentStructure.Project.{ ProjectName, SearchDomain }
import edison.yaml.project.ParserHelpers._
import edison.yaml.project.ProjectDefinitionParser.ParseResult

import scala.util.Try

object ProjectParser {
  def parse(root: Any): ParseResult[domain.Project] = for {
    mapping <- Project.parse(root)
    projectName <- ProjectName.parse(mapping)
    searchDomain <- SearchDomainParser.parse(mapping)
  } yield domain.Project(projectName, searchDomain)
}

object SearchDomainParser {
  def parse(project: Mapping): ParseResult[domain.SearchDomain] = Try {
    val paramDefs = SearchDomain.parse(project).get.map(ParamDefParser.parse) map { _.get }
    domain.SearchDomain(domain.ParamDefs(paramDefs: _*))
  }
}

object ParamDefParser {
  def parse(paramDef: Any): ParseResult[domain.ParamDef] = for {
    paramDef <- ParamDef.parse(paramDef)
    name <- ParamName.parse(paramDef)
    paramDomain <- ParamDomain.parse(paramDef) flatMap ParamDomainParser.parse
  } yield domain.ParamDef(name, paramDomain)
}

object ParamDomainParser {
  def parse(paramDomain: Mapping): ParseResult[domain.ParamDomain] = Type.parse(paramDomain) flatMap {
    case "Integer" => ParamDomainIntegerParser.parse(paramDomain)
    case "Enum" => ParamDomainEnumParser.parse(paramDomain)
    case _ => parseError(ParamDomain.Type.Invalid)
  }
}

object ParamDomainIntegerParser {
  def parse(paramDomain: Mapping): ParseResult[domain.ParamDomainInteger] =
    Try { domain.ParamDomainInteger(Range.inclusive(Start(paramDomain), End(paramDomain), Step(paramDomain))) }
}

object ParamDomainEnumParser {
  def parse(paramDomain: Mapping): ParseResult[domain.ParamDomainEnum[_]] =
    Values.parse(paramDomain) map { seq => domain.ParamDomainEnum(parseEnumeration(seq)) }
}

