package edison.search.tree.transform

import edison.search.Samples
import edison.search.tree.IntegerTree

trait TestTree {

  // The input tree is:
  //     1
  //   2   3
  //      4 5

  val n5 = IntegerTree.empty(Range(500, 600))
  val n4 = IntegerTree.empty(Range(400, 500))
  val n3 = IntegerTree(List(n4, n5), Range(400, 600), Samples.empty)
  val n2 = IntegerTree.empty(Range(200, 300))
  val n1 = IntegerTree(List(n2, n3), Range(200, 600), Samples.empty)
}
