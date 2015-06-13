package edison.search.algorithm

import edison.search.Result
import edison.search.tree.Helpers.TreePrettyPrinter
import edison.search.tree._
import edison.search.tree.select.ClosestRangeSelector
import edison.search.tree.verifier.BasicVerifier
import edison.util.SmartSpec
import org.scalatest.{ Inside, Inspectors }

/**
 * This test uses UCT algorithm to sample a quadratic function with global maximum 0 at x0 == 70
 */
class ParabolaTest extends SmartSpec with Inspectors with Inside {
  def parabola(x0: Int): Int => Result = x => -1 * Math.pow(x - x0, 2)
  val stepsNum = 200

  val ctx = new UctTestContext(alpha = 10, expandThreshold = 20)
  import ctx._

  val expandedTree = performSteps(stepsNum)(algorithm, root, parabola(x0 = 70))

  val nodeWith1 = ClosestRangeSelector(1)(expandedTree).tree.asInstanceOf[IntegerTree]
  val nodeWith60 = ClosestRangeSelector(60)(expandedTree).tree.asInstanceOf[IntegerTree]
  val nodeWith69 = ClosestRangeSelector(69)(expandedTree).tree.asInstanceOf[IntegerTree]
  val nodeWith70 = ClosestRangeSelector(70)(expandedTree).tree.asInstanceOf[IntegerTree]
  val nodeWith71 = ClosestRangeSelector(71)(expandedTree).tree.asInstanceOf[IntegerTree]
  val nodeWith100 = ClosestRangeSelector(100)(expandedTree).tree.asInstanceOf[IntegerTree]

  val secondaryNodes = List(nodeWith1, nodeWith60, nodeWith69, nodeWith71, nodeWith100)

  behavior of "UctAlgorithm after 200 steps of searching the maximum of f(x) = -(x-70)^2"

  it must "completely split nodes that contain global maximum" in {
    nodeWith70.range shouldBe Range.inclusive(70, 70)
  }

  it should "not explore ranges that do not give good results" in {
    nodeWith1.range.size shouldBe >(10)
    nodeWith100.range.size shouldBe >(10)
    nodeWith60.range.size shouldBe >(5)
  }

  it must "explore ranges close to global maximum" in {
    nodeWith69.range.size shouldBe <(10)
    nodeWith71.range.size shouldBe <(10)
  }

  it must "estimate f(x0) correctly" in {
    nodeWith70.samples.mean.value shouldBe (0.0 +- 0.01)
  }

  it must "compute mean and UCB of all secondary nodes correctly" in {
    withClue(expandedTree.shortJson) {
      forAll(secondaryNodes) { secondaryNode =>
        nodeWith70.samples.mean shouldBe >(secondaryNode.samples.mean)
        nodeWith70.samples.ucb(1.0, stepsNum) shouldBe >(secondaryNode.samples.ucb(1.0, stepsNum))
      }
    }
  }

  it must "create a tree that passes tree verification" in {
    new BasicVerifier(expandedTree).verify
  }
}

