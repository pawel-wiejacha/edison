package edison.search.tree.builder

import edison.model.domain.{ ParamDomainInteger, ParamDef, ParamDefs, SearchDomain }
import edison.search.tree.IntegerTree
import edison.util.SmartSpec

class TreeBuilderTest extends SmartSpec {
  val builder = new TreeBuilder

  behavior of "TreeBuilder"

  it must "handle search domain with a single integer parameter" in {
    val paramDef = ParamDef("param1", new ParamDomainInteger(Range(1, 10, 2)))
    val domain = SearchDomain(ParamDefs(paramDef))
    val tree = builder.build(domain)

    tree.children shouldBe empty
    tree.samples shouldBe empty
    tree.asInstanceOf[IntegerTree].range shouldBe Range(1, 10, 2)
  }

}
