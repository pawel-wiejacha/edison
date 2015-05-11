package edison.search.tree

import edison.search.Samples
import edison.util.SmartSpec

class TreeTest extends SmartSpec {
  behavior of "Tree"

  it must "unapply to Leaf and Node correctly" in {
    val leaf = IntegerTree(List.empty, Range(1, 2), Samples.empty)
    val node = IntegerTree(List(leaf, leaf), Range(1, 2), Samples.empty)

    val Node(children) = node
    val Leaf() = leaf

    children shouldBe List(leaf, leaf)
  }
}
