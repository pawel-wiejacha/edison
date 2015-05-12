package edison.search.tree.transform

import edison.search.Samples
import edison.search.tree.{ IntegerTree, Tree }
import edison.util.SmartSpec

class ExpanderTest extends SmartSpec {
  behavior of "Tree Expander"

  // The input tree is:
  //     1
  //   2   3
  //      4 5

  val n5 = IntegerTree.empty(Range(500, 600))
  val n4 = IntegerTree.empty(Range(400, 500))
  val n3 = IntegerTree(List(n4, n5), Range(400, 600), Samples.empty)
  val n2 = IntegerTree.empty(Range(200, 300))
  val n1 = IntegerTree(List(n2, n3), Range(200, 600), Samples.empty)

  it must "ignore leaves that do not match the predicate" in {
    Expander(x => false)(n2) shouldBe n2
  }

  it must "ignore nodes, even if they match the predicate" in {
    Expander(tree => tree.children.nonEmpty)(n3) shouldBe n3
  }

  it must "split leaves that match the predicate" in {
    val n2split = n2.split
    n2split.children.size shouldBe 2

    Expander(tree => tree == n2)(n2) shouldBe n2split
  }

  it must "split newly created leaves recursively" in {
    // split n2
    val n2split = n2.split

    // then split its children
    val n2Asplit = n2split.children(0).split
    val n2Bsplit = n2split.children(1).split
    val n2splitSplit = n2split.withChildren(List(n2Asplit, n2Bsplit))

    val predicate = { tree: Tree => tree.asInstanceOf[IntegerTree].range.size > 49 }
    Expander(predicate)(n2) shouldBe n2splitSplit
  }

  it must "split tree recursively and propagate changes up" in {
    val changedN1 = n1.withChildren(List(n2.split, n3))

    Expander(tree => tree == n2)(n1) shouldBe changedN1
  }

}
