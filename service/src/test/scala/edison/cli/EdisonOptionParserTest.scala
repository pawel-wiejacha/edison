package edison.cli

import edison.search.IntValue
import edison.util.SmartSpec

class EdisonOptionParserTest extends SmartSpec {
  behavior of "EdisonOptionParser when parsing command line options"

  case class ParseResult(config: Option[Config], errors: String)

  def parse(args: String*): ParseResult = {
    val parser = new OptionParserForTests
    val config = parser.parse(args, Config())
    ParseResult(config, parser.errors)
  }

  it must "require --definitionFile option to be provided" in {
    val ParseResult(config, errors) = parse()
    config shouldBe None
    errors should include("--definitionFile")
  }

  it must "require --definitionFile and --journalFile options to be provided" in {
    val ParseResult(config, errors) = parse("--definitionFile", "file")
    config shouldBe None
    errors should include("--journalFile")
  }

  it must "require an action to be provided" in {
    val ParseResult(config, errors) = parse("--definitionFile", "file1", "--journalFile", "file2")
    config shouldBe None
    errors should include("No action provided")
  }

  it must "accept `sample` command" in {
    val ParseResult(config, errors) = parse("sample", "-d", "file1", "-j", "file2")
    config.value shouldBe Config("file1", "file2", GenerateSampleAction())
    errors shouldBe empty
  }

  it must "accept `store` command" in {
    val ParseResult(config, errors) = parse("store", "-d", "file1", "-j", "file2", "-s", "sample", "-r", "0.5")
    config.value shouldBe Config("file1", "file2", StoreResultAction("sample", 0.5))
    errors shouldBe empty
  }

  it must "require all `store` command options to be provided" in {
    val ParseResult(config, errors) = parse("store", "-d", "file1", "-j", "file2", "-s", "33")
    config shouldBe None
    errors should include("--result")
  }

}
