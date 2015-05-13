package edison.search

import edison.util.SmartSpec
import org.scalactic.TolerantNumerics

class UcbTest extends SmartSpec {

  val alpha = 2
  val countsToTest = List(1, 2, 3, 4, 10, 100, 1000, 10000)

  implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.001)

  implicit class UcbHelper(samples: Samples) {
    def ucb(parentVisitNum: Int): Double = samples.ucb(alpha, parentVisitNum).get
  }

  def createSamples(mean: Result, count: Int) = Samples(List.fill(count)(Sample(1, mean)))

  behavior of "Samples.ucb"

  it must "return None for an empty samples" in {
    Samples.empty.ucb(alpha, 5) shouldBe None
  }

  it must "favor less explored node, when both means are equal" in {
    for (count <- countsToTest) {
      val lessExplored = createSamples(mean = 10.0, count = count)
      val moreExplored = createSamples(mean = 10.0, count = count + 1)
      val n = count * 2 + 1

      lessExplored.mean shouldBe moreExplored.mean
      lessExplored.ucb(n) shouldBe >(moreExplored.ucb(n))
    }
  }

  it must "favor a node with better mean, when both nodes are equally explored" in {
    for (count <- countsToTest) {
      val better = createSamples(mean = 10.0, count = count)
      val worse = createSamples(mean = 9.9, count = count)
      val n = count * 2

      better.ucb(n) shouldBe >(worse.ucb(n))
    }
  }

  it must "converge to mean" in {
    val ucbs = countsToTest map { count => createSamples(mean = 10.0, count = count).ucb(count * 5) }

    ucbs.reverse should be(sorted)
    ucbs.head shouldBe >(ucbs.last)
    ucbs.head shouldBe >(11.0)
    (ucbs.last - 10.0) shouldBe <(0.1)
  }

  it must "re-explore ignored nodes" in {
    val explored = createSamples(mean = 10.0, count = 1000)
    val ignored = createSamples(mean = 9.0, count = 10)

    explored.ucb(1000) shouldBe >(ignored.ucb(10))
    explored.ucb(1000) shouldBe <(ignored.ucb(1000))
  }

  it must "favor exploration over exploitation when alpha gets bigger" in {
    val good = createSamples(mean = 10.0, count = 20)
    val bad = createSamples(mean = 9.0, count = 10)

    good.ucb(alpha = 2, 30) shouldBe >(bad.ucb(alpha = 2, 30))
    good.ucb(alpha = 8, 30) shouldBe <(bad.ucb(alpha = 8, 30))
  }

}
