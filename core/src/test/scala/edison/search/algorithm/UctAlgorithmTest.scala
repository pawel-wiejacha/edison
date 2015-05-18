package edison.search.algorithm

import edison.search.tree._
import edison.search.tree.select.ClosestRangeSelector
import edison.search.tree.verifier.BasicVerifier
import edison.search.{ Result, Sample }
import edison.util.SmartSpec
import org.scalatest.{ Inside, Inspectors }

class UctAlgorithmTest extends SmartSpec with Inspectors with Inside {

  class AlgTest(alpha: Double = 2, expandThreshold: Int = 5) {
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

  behavior of "UctAlgorithm"

  it must "create correct samples when sampling a leaf" in new AlgTest() {
    val samples = Range(0, 2000).map(x => algorithm.sample(root).asInt)

    forAll(samples) { sample =>
      sample shouldBe >=(0)
      sample shouldBe <(200)
    }

    (samples.sum / samples.size.toDouble) shouldBe (50.0 +- 3)
    samples.distinct.size shouldBe >(98)
  }

  it must "sample from leaf with the highest ucb when sampling a node" in new AlgTest() {
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

  it must "be able to update a unit node" in new AlgTest() {
    def constantFunction(result: Result): Int => Result = x => result

    val updatedTree = performSteps(10)(algorithm, root, constantFunction(50))
    updatedTree.samples.size shouldBe 10
    updatedTree.samples.mean.value shouldBe 50.0
    updatedTree.samples.sd.value shouldBe 0
  }

  it must "expand unit node when threshold is exceeded" in new AlgTest(expandThreshold = 3) {
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

  it must "expand tree nodes that produces better results" in new AlgTest(expandThreshold = 10) {
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

  /** This test context uses UCT algorithm to sample a quadratic function with global maximum 0 at x0 == 70 */
  class ParabolaTest extends AlgTest(alpha = 3, expandThreshold = 10) {
    def parabola(x0: Int): Int => Result = x => -1 * Math.pow((x - x0), 2)

    val expandedTree = performSteps(100)(algorithm, root, parabola(x0 = 70))

    val nodeWith1 = ClosestRangeSelector(1)(expandedTree).tree.asInstanceOf[IntegerTree]
    val nodeWith60 = ClosestRangeSelector(60)(expandedTree).tree.asInstanceOf[IntegerTree]
    val nodeWith69 = ClosestRangeSelector(69)(expandedTree).tree.asInstanceOf[IntegerTree]
    val nodeWith70 = ClosestRangeSelector(70)(expandedTree).tree.asInstanceOf[IntegerTree]
    val nodeWith71 = ClosestRangeSelector(71)(expandedTree).tree.asInstanceOf[IntegerTree]
    val nodeWith100 = ClosestRangeSelector(100)(expandedTree).tree.asInstanceOf[IntegerTree]

    val secondaryNodes = List(nodeWith1, nodeWith60, nodeWith69, nodeWith71, nodeWith100)
  }

  behavior of "UctAlgorithm after 100 steps of searching the maximum of f(x) = -(x-70)^2"

  it must "completely split nodes that contain global maximum" in new ParabolaTest {
    nodeWith70.range shouldBe Range.inclusive(70, 70)
  }

  it should "not explore ranges that do not give good results" in new ParabolaTest {
    nodeWith1.range.size shouldBe >(20)
    nodeWith100.range.size shouldBe >(20)
    nodeWith60.range.size shouldBe >(10)
  }

  it must "explore ranges close to global maximum" in new ParabolaTest {
    nodeWith69.range.size shouldBe <(10)
    nodeWith71.range.size shouldBe <(10)
  }

  it must "estimate f(x0) correctly" in new ParabolaTest {
    nodeWith70.samples.mean.value shouldBe (0.0 +- 0.01)
  }

  it must "compute mean and UCB of all secondary nodes correctly" in new ParabolaTest {
    forAll(secondaryNodes) { secondaryNode =>
      nodeWith70.samples.mean shouldBe >(secondaryNode.samples.mean)
      nodeWith70.samples.ucb(2.0, 100) shouldBe >(secondaryNode.samples.ucb(2.0, 100))
    }
  }

  it must "create a tree that passes tree verification" in new ParabolaTest {
    new BasicVerifier(expandedTree).verify
  }
}

