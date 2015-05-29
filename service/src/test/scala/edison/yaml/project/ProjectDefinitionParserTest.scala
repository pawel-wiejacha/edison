package edison.yaml.project

import edison.model.domain._
import edison.util.IntBytes.IntBytes
import edison.util.SmartFreeSpec
import edison.yaml.ScalaYaml
import edison.yaml.project.ProjectDefinitionParser.ParseResult

import scala.language.postfixOps

class ProjectDefinitionParserTest extends SmartFreeSpec {

  val sampleProjectYaml =
    """
      |project-name: cache-tuning
      |search-domain:
      |  -
      |    param-name: CacheSize
      |    domain:
      |      type: Integer
      |      start: 4194304
      |      end: 104857600
      |      step: 1048576
      |    default-value: 20971520
      |  -
      |    param-name: EvictionPolicy
      |    domain:
      |      type: Enum
      |      values: [LRU, FIFO, SecondChance]
      |    default-value: FIFO
      |# Comments are supported.
      |# But evaluation scenarios are not supported yet.
      |## evaluation-scenarios:
      |##  - scenario:
      |##    name: "5 writers, sequential reads"
      |##    categories: # user defined categories
      |##      concurrency: small
      |##      read-pattern: sequential
      |##  - scenario:
      |##    name: "5 writers, random reads 5 MB"
      |##    categories: # user defined categories
      |##      concurrency: small
      |##      read-pattern: random
    """.strip

  val parser = new ProjectDefinitionParser

  def expectError[T](expectedErrorCategory: ErrorCategory)(parseResult: ParseResult[T]): Unit = {
    parseResult.failure.exception.getMessage should include(expectedErrorCategory.errorName)
  }

  implicit class YamlStringToMapping(yamlStr: String) {
    def asMapping = ScalaYaml.load(yamlStr.strip).asInstanceOf[Map[String, Any]]
  }

  def enumParamDomainValues(paramDomain: ParamDomain): Set[String] =
    paramDomain.asInstanceOf[ParamDomainEnum[Enumeration]].enumeration.values.map((_: Any).toString)

  "ProjectDefinitionParser" - {
    "when parsing a field" - {
      "must parse correct object field - String" in {
        val value: String = ParserHelpers.getField[String]("key: value".asMapping, "key").get
        value shouldBe "value"
      }
      "must parse correct object field - Int" in {
        val value: Int = ParserHelpers.getField[Int]("key: 123".asMapping, "key").get
        value shouldBe 123
      }
      "must parse correct object field - Object" in {
        val mapping =
          """
            |key:
            |  innerKey: innerValue
            |  intKey: 333
          """.asMapping
        ParserHelpers.getField[Map[String, Any]](mapping, "key").get shouldBe Map("innerKey" -> "innerValue", "intKey" -> 333)
      }
      "must report missing object field" in {
        ParserHelpers.getField[String]("key2: value".asMapping, "key").failure.exception shouldBe a[NoSuchElementException]
      }
      "must report invalid type - Int" in {
        ParserHelpers.getField[Int]("key: \"555\"".asMapping, "key").failure.exception shouldBe a[IllegalArgumentException]
      }
      "must convert Int to String, if needed" in {
        ParserHelpers.getField[String]("key: 555".asMapping, "key").get shouldBe "555"
      }
      "must report invalid type - Map[Int, Any] instead of Map[String, Any]" in {
        ParserHelpers.getField[Map[String, Any]]("key:\n  123: innerValue".asMapping, "key").failure.exception shouldBe a[IllegalArgumentException]
      }
      "must report invalid type - List[String] instead of Map[String, Any]" in {
        ParserHelpers.getField[Map[String, Any]]("key:\n- elem".asMapping, "key").failure.exception shouldBe a[IllegalArgumentException]
      }
      "must report invalid type - String instead of Map[String, Any]" in {
        ParserHelpers.getField[Map[String, Any]]("key: value".asMapping, "key").failure.exception shouldBe a[IllegalArgumentException]
      }
    }

    "must report an error when it encounters" - {
      "invalid YAML document" in {
        expectError(Errors.YamlError) { parser.parse("!not-a-yaml") }
      }

      "an empty document" in {
        expectError(DocumentStructure.Project.NotAMapping) { parser.parse("") }
      }

      "a map with invalid key type" in {
        expectError(DocumentStructure.Project.NotAMapping) { parser.parse("123: 'key is not a string'") }
      }

      "missing project name" in {
        expectError(DocumentStructure.Project.ProjectName.Missing) { parser.parse("search-domain: 123") }
      }

      "missing search domain" in {
        expectError(DocumentStructure.Project.SearchDomain.Missing) { parser.parse("project-name: 123") }
      }

      "invalid search domain type" in {
        expectError(DocumentStructure.Project.SearchDomain.Invalid) { parser.parse("project-name: 123\nsearch-domain: 123") }
      }
    }

    "when parsing an IntegerParamDomain" - {
      val correctYaml =
        """
          |type: Integer
          |start: 1
          |end: 1000
          |step: 2
        """

      "must report error on missing `start` field" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainInteger.Start.Missing) {
          ParamDomainParser.parse(correctYaml.replace("start", "missing").asMapping)
        }
      }

      "must report error on missing `end` field" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainInteger.End.Missing) {
          ParamDomainParser.parse(correctYaml.replace("end", "missing").asMapping)
        }
      }

      "must report error on missing `step` field" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainInteger.Step.Missing) {
          ParamDomainParser.parse(correctYaml.replace("step", "missing").asMapping)
        }
      }

      "must report error on invalid `step` field" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainInteger.Step.Invalid) {
          ParamDomainParser.parse(correctYaml.replace("step: 2", "step: Invalid").asMapping)
        }
      }

      "must parse correct IntegerParamDomain yaml document" in {
        val domain = ParamDomainParser.parse(correctYaml.asMapping).get.asInstanceOf[ParamDomainInteger]
        domain.range shouldBe Range.inclusive(1, 1000, 2)
      }
    }

    "when parsing an EnumParamDomain" - {
      "must report error on missing `values` field" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainEnum.Values.Missing) {
          ParamDomainParser.parse(
            """
              |type: Enum
              |missing-values: [Foo, Bar]
            """.asMapping
          )
        }
      }

      "must report error on invalid `values` field" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainEnum.Values.Invalid) {
          ParamDomainParser.parse(
            """
              |type: Enum
              |values: { foo: bar }
            """.asMapping
          )
        }
      }

      "must parse correct EnumParamDomain yaml document" in {
        val domain = ParamDomainParser.parse(
          """
            |type: Enum
            |values: [Foo, Bar]
          """.asMapping
        ).get
        enumParamDomainValues(domain) shouldBe Set("Foo", "Bar")
      }
    }

    "must propagate errors from nested parsers" - {
      "missing ParamDef name" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamName.Missing) {
          parser.parse(
            """
              |project-name: 123
              |search-domain:
              |  -
              |    missing-param-name: foo
            """.strip
          )
        }
      }

      "missing ParamDef domain type" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.Type.Missing) {
          parser.parse(
            """
              |project-name: 123
              |search-domain:
              |  -
              |    param-name: foo
              |    domain:
              |      foo: bar
            """.strip
          )
        }
      }

      "invalid ParamDef domain type " in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.Type.Invalid) {
          parser.parse(
            """
              |project-name: 123
              |search-domain:
              |  -
              |    param-name: foo
              |    domain:
              |      type: Invalid
            """.strip
          )
        }
      }

      "missing IntegerParamDomain start" in {
        expectError(DocumentStructure.Project.SearchDomain.ParamDef.ParamDomain.ParamDomainInteger.Start.Missing) {
          parser.parse(
            """
              |project-name: 123
              |search-domain:
              |  -
              |    param-name: foo
              |    domain:
              |      type: Integer
            """.strip
          )
        }
      }
    }

    "when parsing a valid sample project file" - {
      val project = parser.parse(sampleProjectYaml).get

      "must parse project name" in {
        project.name shouldBe "cache-tuning"
      }

      "must parse search domain - integer domain" in {
        project.searchDomain.paramDefs(0) shouldBe ParamDef("CacheSize", ParamDomainInteger(Range.inclusive(4 MB, 100 MB, 1 MB)))
      }

      "must parse search domain - enum domain" in {
        val paramDef1 = project.searchDomain.paramDefs(1)
        paramDef1.name shouldBe "EvictionPolicy"
        paramDef1.domain shouldBe a[ParamDomainEnum[_]]
        enumParamDomainValues(paramDef1.domain) shouldBe Set("FIFO", "LRU", "SecondChance")
      }
    }

    "must ignore unknown fields" in { // TODO this is not a feature
      val mapping =
        """
        |type: Enum
        |values: [Foo, Bar]
      """.asMapping

      ParamDomainParser.parse(mapping).get shouldBe a[ParamDomainEnum[_]]
    }

  }

}
