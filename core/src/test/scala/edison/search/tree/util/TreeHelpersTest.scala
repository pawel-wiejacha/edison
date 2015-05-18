package edison.search.tree.util

import edison.search.tree.Tree
import edison.search.tree.transform.TestTree
import edison.util.SmartSpec
import org.scalamock.scalatest.MockFactory

class TreeHelpersTest extends SmartSpec with TestTree with MockFactory {
  behavior of "TreeHelpers.foreach"

  it must "perform pre-order tree traversal" in {
    val visitor = mockFunction[Tree, Unit]
    List(n1, n2, n3, n4, n5).foreach(node => visitor.expects(node))
    TreeHelpers.foreach(n1)(visitor)
  }

}
