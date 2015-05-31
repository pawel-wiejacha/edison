package edison.search.tree.builder

import edison.model.domain.{ ParamDomainInteger, SearchDomain }
import edison.search.tree.{ IntegerTree, Tree }

/** Creates an initial search tree that is based on a given SearchDomain */
class TreeBuilder {
  def build(searchDomain: SearchDomain): Tree = {
    assert(searchDomain.paramDefs.size > 0, "Empty search domain")
    assert(searchDomain.paramDefs.size < 2, "Currently only one-dimensional search spaces are supported")

    val paramDef = searchDomain.paramDefs(0)

    paramDef.domain match {
      case ParamDomainInteger(range) => IntegerTree.empty(range)
      case _ => assert(false, "Currently only integer domains are supported"); null
    }
  }
}

