package edison.search.algorithm

import edison.search.Samples
import edison.search.tree._
import edison.search.tree.select.Selector
import edison.util.SmartSpec
import org.scalamock.scalatest.MockFactory

class UctSelectionContextTest extends SmartSpec with MockFactory {

  val algorithm = new UctAlgorithm(UctConfig(alpha = 2.0, expandThreshold = 10))

  val nodeWithNoSamples = IntegerTree.empty(Range(1, 2))
  val nodeWithGoodSample = IntegerTree(Range(1, 2), Nil, samples = Samples(1 -> 100.0))
  val nodeWithBadSample = IntegerTree(Range(1, 2), Nil, samples = Samples(1 -> 10.0))

  behavior of "UcbSelectionContext"

  it must "create orderings that prefer nodes with no samples" in {
    val root = IntegerTree(Range.inclusive(1, 2), List.empty, samples = Samples(1 -> 100.0))
    val ordering = algorithm.UcbSelectionContext(root).getOrdering

    ordering.compare(nodeWithNoSamples, nodeWithGoodSample) shouldBe -1
    ordering.compare(nodeWithGoodSample, nodeWithNoSamples) shouldBe 1
    ordering.compare(nodeWithNoSamples, nodeWithNoSamples) shouldBe 0
  }

  it must "create orderings that prefer nodes with higher ucb" in {
    val root = IntegerTree(Range.inclusive(1, 2), List.empty, samples = Samples(1 -> 100.0, 1 -> 10.0))
    val ordering = algorithm.UcbSelectionContext(root).getOrdering

    ordering.compare(nodeWithGoodSample, nodeWithBadSample) shouldBe -1
    ordering.compare(nodeWithBadSample, nodeWithGoodSample) shouldBe 1
    ordering.compare(nodeWithGoodSample, nodeWithGoodSample) shouldBe 0
  }

  it must "compute UCB using correct total number of samples" in {
    class MockableSamples extends Samples(Nil)
    val samplesMock = mock[MockableSamples]
    (samplesMock.ucb _).expects(2.0, 2).returning(Some(100.0))

    val nodeWithGoodSampleAndCheck = nodeWithGoodSample.updated(samplesMock)
    val root = IntegerTree(
      Range.inclusive(1, 2),
      List(nodeWithBadSample, nodeWithGoodSampleAndCheck),
      Samples(1 -> 100.0, 1 -> 10.0)
    )

    val selector = Selector(algorithm.UcbSelectionContext(root))
    selector(root).tree shouldBe nodeWithGoodSampleAndCheck
  }
}

