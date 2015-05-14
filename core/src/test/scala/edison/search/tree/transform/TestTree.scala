package edison.search.tree.transform

import edison.search.tree.IntegerTree

trait TestTree {

  // The input tree is:
  //     1        ranges: [200; 600)
  //   2   3      ranges: [200; 300) [300; 600)
  //      4 5     ranges: [300; 500) [500; 600)

  val n5 = IntegerTree.empty(Range(500, 600))
  val n4 = IntegerTree.empty(Range(300, 500))
  val n3 = IntegerTree(Range(300, 600), List(n4, n5))
  val n2 = IntegerTree.empty(Range(200, 300))
  val n1 = IntegerTree(Range(200, 600), List(n2, n3))
}
