package edison.search.tree

import edison.util.SmartSpec

class TreeTest extends SmartSpec {
  behavior of "Tree"

  it must "unapply to Leaf and Node correctly" in {
    val leaf = IntegerTree(Range(1, 2), List.empty)
    val node = IntegerTree(Range(1, 2), List(leaf, leaf))

    val Node(children) = node
    val Leaf() = leaf

    children shouldBe List(leaf, leaf)
  }
}
