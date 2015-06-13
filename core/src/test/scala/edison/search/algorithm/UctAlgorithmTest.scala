package edison.search.algorithm

import edison.search.tree._
import edison.search.{ Result, Sample }
import edison.util.SmartSpec
import edison.util.TestHelpers.fold
import org.scalatest.{ Inside, Inspectors }

class UctTestContext(alpha: Double = 2, expandThreshold: Int = 5) {
  val algorithm = new UctAlgorithm(UctConfig(alpha = alpha, expandThreshold = expandThreshold))
  val root = IntegerTree.empty(Range.inclusive(1, 100))

  def updateAndExpand(samples: Sample*): Tree => Tree = { tree =>
    samples.foldLeft(tree)({ (acc, sample) =>
      algorithm.expand(algorithm.update(acc, sample))
    })
  }

  /**
   * Performs the whole algorithm step (sample, evaluate, update, expand)
   *
   * @param resultFunction - function used to evaluate sampled values
   */
  def performSteps(steps: Int)(alg: UctAlgorithm, tree: Tree, resultFunction: Int => Result): Tree = {
    fold(steps)(tree, { oldTree: Tree =>
      val value = alg.sample(oldTree)
      val result = resultFunction(value.asInt)
      updateAndExpand(Sample(value, result))(oldTree)
    })
  }
}

class UctAlgorithmTest extends SmartSpec with Inspectors with Inside {

  behavior of "UctAlgorithm"

  it must "create correct samples when sampling a leaf" in new UctTestContext() {
    val samples = Range(0, 2000).map(x => algorithm.sample(root).asInt)

    forAll(samples) { sample =>
      sample shouldBe >=(0)
      sample shouldBe <(200)
    }

    (samples.sum / samples.size.toDouble) shouldBe (50.0 +- 3)
    samples.distinct.size shouldBe >(98)
  }

  it must "sample from leaf with the highest ucb when sampling a node" in new UctTestContext() {
    val tree = IntegerTree(
      Range(1, 4),
      List(
        IntegerTree.empty(Range(1, 2)),
        IntegerTree.empty(Range(2, 3)),
        IntegerTree.empty(Range(3, 4))
      )
    )

    val updatedTree = updateAndExpand(1 -> 5.0, 3 -> 5.0, 2 -> 10.0)(tree)
    val samples = Range(0, 100).map(x => algorithm.sample(updatedTree).asInt)

    forAll(samples) { sample => sample shouldBe 2 }
  }

  it must "be able to update a unit node" in new UctTestContext() {
    def constantFunction(result: Result): Int => Result = x => result

    val updatedTree = performSteps(10)(algorithm, root, constantFunction(50))
    updatedTree.samples.size shouldBe 10
    updatedTree.samples.mean.value shouldBe 50.0
    updatedTree.samples.sd.value shouldBe 0
  }

  it must "expand unit node when threshold is exceeded" in new UctTestContext(expandThreshold = 3) {
    val tree2 = fold(2)(root, updateAndExpand(1 -> 10.0))
    tree2.children shouldBe empty

    val tree3 = updateAndExpand(100 -> 20.0)(tree2)

    inside(tree3) {
      case Node(_, n1, n2) => {
        inside(n1) { case Leaf(IRange(1, 50)) => n1.samples.results shouldBe List(10, 10) }
        inside(n2) { case Leaf(IRange(51, 100)) => n2.samples.results shouldBe List(20) }
      }
    }
  }

  it must "expand tree nodes that produces better results" in new UctTestContext(expandThreshold = 10) {
    def linear: Int => Result = x => 1.0 * x

    val expandedTree = performSteps(100)(algorithm, IntegerTree.empty(Range(1, 3)), linear)

    val n1 @ Node(IRange(1, 2),
      n2 @ Leaf(IRange(1, 1)),
      n3 @ Leaf(IRange(2, 2))
      ) = expandedTree

    n1.samples.size shouldBe 100
    (n2.samples ::: n3.samples).values should contain theSameElementsAs n1.samples.values
    n2.samples.mean.value shouldBe <(n3.samples.mean.value)
    n2.samples.size shouldBe <(n3.samples.size)
  }
}

