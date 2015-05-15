package edison.search.tree

import edison.search.tree.transform.TestTree
import edison.util.SmartSpec

class TreeTest extends SmartSpec with TestTree {
  behavior of "Tree"

  it must "unapply to Leaf, if it's a leaf" in {
    val Leaf(IRange(200, 299)) = n2
  }

  it must "unapply to Node, if it's a node" in {
    val Node(IRange(200, 599), x, y) = n1
    (x, y) shouldBe (n2, n3)
  }

  it must "not unapply to Leaf, if it's not a leaf" in {
    intercept[MatchError] { val Node(_) = n2 }
    intercept[MatchError] { val Node(_, _*) = n2 }
  }

  it must "not unapply to Node, if it's not a node" in {
    intercept[MatchError] { val Leaf(_) = n1 }
  }

  it must "unapply to Tree if it's a node" in {
    val Tree(IRange(200, 599), x, y) = n1
    (x, y) shouldBe (n2, n3)
  }

  it must "unapply to Tree if it's a leaf" in {
    val Tree(IRange(200, 299)) = n2
  }

  it must "allow for pattern matching with wildcards (range)" in {
    val Node(IRange(200, _), x, y) = n1
    (x, y) shouldBe (n2, n3)
  }

  it must "allow for pattern matching with wildcards (a child)" in {
    val Node(IRange(200, _), _, y) = n1
    y shouldBe n3
  }

  it must "allow for pattern matching with wildcards (some children)" in {
    val Node(IRange(200, _), x, _*) = n1
    x shouldBe n2
  }

  it must "allow for pattern matching with wildcards (all children)" in {
    val Node(IRange(200, _), children @ _*) = n1
    children shouldBe Seq(n2, n3)
  }

  it must "allow tree deconstruction using pattern matching" in {
    val x1 @ Node(IRange(200, 599),
      x2 @ Leaf(IRange(200, 299)),
      x3 @ Node(IRange(300, 599),
        x4 @ Leaf(IRange(300, 499)),
        x5 @ Leaf(IRange(500, 599)))
      ) = n1

    (x1, x2, x3, x4, x5) shouldBe (n1, n2, n3, n4, n5)
  }
}
