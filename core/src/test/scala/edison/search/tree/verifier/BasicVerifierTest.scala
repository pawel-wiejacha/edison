package edison.search.tree.verifier

import edison.search.Samples
import edison.search.tree.IntegerTree
import edison.util.SmartSpec

class BasicVerifierTest extends SmartSpec {
  behavior of "BasicVerifier"

  it must "detect inconsistent ranges - non-disjoint" in {
    val root = IntegerTree(Range(1, 10), List(
      IntegerTree.empty(Range(1, 5)),
      IntegerTree.empty(Range(4, 10))
    ))

    intercept[AssertionError] { new BasicVerifier(root).verify }
  }

  it must "detect inconsistent ranges - gaps" in {
    val root = IntegerTree(Range(1, 10), List(
      IntegerTree.empty(Range(1, 3)),
      IntegerTree.empty(Range(4, 5)),
      IntegerTree.empty(Range(6, 10))
    ))

    intercept[AssertionError] { new BasicVerifier(root).verify }
  }

  it must "detect inconsistent ranges - different steps" in {
    val root = IntegerTree(Range(1, 10), List(
      IntegerTree.empty(Range(1, 5, step = 2)),
      IntegerTree.empty(Range(5, 10))
    ))

    intercept[AssertionError] { new BasicVerifier(root).verify }
  }

  it must "detect inconsistent ranges - monotonicity" in {
    val root = IntegerTree(Range(1, 10), List(
      IntegerTree.empty(Range(1, 10))
    ))

    intercept[AssertionError] { new BasicVerifier(root).verify }
  }

  it must "pass tree with consistent ranges" in {
    val root = IntegerTree(Range(1, 10), List(
      IntegerTree.empty(Range(1, 5)),
      IntegerTree.empty(Range(5, 10))
    ))

    new BasicVerifier(root).verify
  }

  it must "detect inconsistent samples - additional samples" in {
    val root = IntegerTree(Range(1, 10), List(
      IntegerTree(Range(1, 5), children = Nil, samples = Samples(1 -> 1.0)),
      IntegerTree.empty(Range(5, 10))
    ))

    intercept[AssertionError] { new BasicVerifier(root).verify }
  }

  it must "detect inconsistent samples - missing samples" in {
    val root = IntegerTree(
      Range(1, 10),
      List(IntegerTree.empty(Range(1, 5)), IntegerTree.empty(Range(5, 10))),
      samples = Samples(1 -> 1.0)
    )

    intercept[AssertionError] { new BasicVerifier(root).verify }
  }

  it must "pass tree with consistent samples" in {
    val root = IntegerTree(
      Range(1, 10),
      List(
        IntegerTree(Range(1, 5), children = Nil, samples = Samples(1 -> 1.0)),
        IntegerTree.empty(Range(5, 10))
      ),
      samples = Samples(1 -> 1.0)
    )

    new BasicVerifier(root).verify
  }

}
