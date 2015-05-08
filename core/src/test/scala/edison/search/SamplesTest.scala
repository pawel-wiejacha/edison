package edison.search

import edison.util.SmartSpec
import IntValueImplicits.intToIntValue
import SampleImplicits.pairToSample
import org.scalactic.TolerantNumerics

class SamplesTest extends SmartSpec {

  behavior of "Samples"

  val emptySamples = Samples.empty
  val oneSample10 = Samples(1 -> 10.0)
  val twoSamples10_20 = Samples(1 -> 10.0, 2 -> 20.0)
  val twoSamples10_10 = Samples(1 -> 10.0, 2 -> 10.0)
  val twoSamples10_11 = Samples(1 -> 10.0, 2 -> 11.0)
  val threeSamples3x10 = Samples(1 -> 10.0, 2 -> 10.0, 3 -> 10.0)
  val threeSamples10_30_20 = Samples(1 -> 10.0, 2 -> 30.0, 3 -> 20.0)

  implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.001)

  it must "compute `size` correctly" in {
    emptySamples.size shouldBe 0
    oneSample10.size shouldBe 1
    twoSamples10_10.size shouldBe 2
  }

  it must "compute `min` and `max` correctly" in {
    def minMax(samples: Samples) = (samples.min, samples.max)

    minMax(emptySamples) shouldBe (None, None)
    minMax(oneSample10) shouldBe (Some(10), Some(10))
    minMax(twoSamples10_10) shouldBe (Some(10), Some(10))
    minMax(twoSamples10_20) shouldBe (Some(10), Some(20))
    minMax(threeSamples10_30_20) shouldBe (Some(10), Some(30))
  }

  it must "compute mean correctly" in {
    emptySamples.mean shouldBe None
    oneSample10.mean shouldBe Some(10)
    twoSamples10_10.mean shouldBe Some(10)
    twoSamples10_11.mean shouldBe Some(10.5)
    twoSamples10_20.mean shouldBe Some(15)
    threeSamples10_30_20.mean shouldBe Some(20)
  }

  it must "compute standard deviation and variance correctly" in {
    def varianceSd(samples: Samples) = (samples.variance, samples.sd)

    varianceSd(emptySamples) shouldBe (None, None)
    varianceSd(oneSample10) shouldBe (Some(0), Some(0))
    varianceSd(twoSamples10_10) shouldBe (Some(0), Some(0))

    twoSamples10_20.variance.get should ===((25 + 25) / 2.0)
    twoSamples10_20.sd.get should ===(5.0)

    threeSamples10_30_20.variance.get should ===((100 + 100 + 0) / 3.0)
    threeSamples10_30_20.sd.get should ===(Math.sqrt(200 / 3.0))
  }

  it must "support adding new samples" in {
    val updated1 = emptySamples.add(1 -> 10.0)
    val updated2 = updated1.add(2 -> 20.0)

    updated1 shouldBe oneSample10
    updated1.results shouldBe List(10.0)

    updated2.values should contain theSameElementsAs twoSamples10_20.values
    updated2.results should contain theSameElementsAs twoSamples10_20.results
    updated2.mean shouldBe twoSamples10_20.mean
    updated2.variance shouldBe twoSamples10_20.variance
    updated2.min shouldBe twoSamples10_20.min
    updated2.max shouldBe twoSamples10_20.max
  }
}
