package edison.yaml

import edison.util.SmartSpec
import org.yaml.snakeyaml.error.YAMLException

class YamlLoaderTest extends SmartSpec {
  behavior of "YamlLoader"

  implicit class YamlHelper(str: String) {
    def load(process: Any => Unit): Unit = process(ScalaYaml.load(str.strip))
  }

  it must "parse list of integers" in {
    """
      |- 123
      |- 456
    """.load { yaml =>
      yaml shouldBe List(123, 456)
    }
  }

  it must "parse list of integers, strings and doubles" in {
    """
      |- 123
      |- abc
      |- 0.5
    """.load { yaml =>
      yaml shouldBe List(123, "abc", 0.5)
    }
  }

  it must "parse nested lists" in {
    """
      |- 123
      |- abc
      |-
      |  - 0.5
      |  - 456
    """.load { yaml =>
      yaml shouldBe List(123, "abc", List(0.5, 456))
    }
  }

  it must "parse Map[String, Int]" in {
    """
      |key1: 123
      |key2: 456
    """.load { yaml =>
      yaml shouldBe Map("key1" -> 123, "key2" -> 456)
    }
  }

  it must "parse Map[String, List[Int]]" in {
    """
      |key1: [123, 456]
      |key2:
      |  - 789
      |  - 12
    """.load { yaml =>
      yaml shouldBe Map(
        "key1" -> List(123, 456),
        "key2" -> List(789, 12)
      )
    }
  }

  it must "parse nested Maps" in {
    """
      |key1:
      |  nestedKey1: 123
      |  nestedKey2: 456
      |key2:
      |  nestedKey3: 789
    """.load { yaml =>
      yaml shouldBe Map(
        "key1" -> Map("nestedKey1" -> 123, "nestedKey2" -> 456),
        "key2" -> Map("nestedKey3" -> 789)
      )
    }
  }

  it must "parse list with repeated elements" in {
    """
      |- &repeated
      |  name: repeated element
      |- 123
      |- *repeated
    """.load { yaml =>
      val Vector(a, 123, c) = yaml
      a.asInstanceOf[Map[String, String]]("name") shouldBe "repeated element"
      a shouldBe c
      System.identityHashCode(a) shouldBe System.identityHashCode(c)
    }
  }

  it must "parse sets" in {
    """
      |--- !!set
      |? one
      |? two
      |? one
    """.load { yaml =>
      yaml shouldBe Set("one", "two")
    }
  }

  it should "not support parsing self-recursive lists" in {
    val yamlStr = """
                    |&list
                    |- 123
                    |- 456
                    |- *list
                  """.strip

    val exn = intercept[YAMLException] { ScalaYaml.load(yamlStr) }
    exn.getMessage should include("recursive")
  }
}
