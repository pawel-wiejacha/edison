package edison.search.tree

import edison.search.IntValueImplicits._
import edison.search.SampleImplicits._
import edison.search.{ IntValue, Sample, Samples }
import edison.util.SmartSpec

class IntegerTreeTest extends SmartSpec {
  behavior of "IntegerTree"

  val nodeEven = IntegerTree.empty(Range(10, 21, 2))
  val node1 = IntegerTree.empty(Range.inclusive(1, 1))
  val node2 = IntegerTree.empty(Range.inclusive(1, 2))
  val node3 = IntegerTree.empty(Range.inclusive(1, 3))
  val node4 = IntegerTree.empty(Range.inclusive(1, 4))
  val node5 = IntegerTree.empty(Range.inclusive(1, 5))

  it must "implement `contains` correctly " in {
    List(10, 12, 18, 20) map { value =>
      nodeEven.contains(IntValue(value)) shouldBe true
    }

    List(9, 11, 13, 21, 22) map { value =>
      nodeEven.contains(IntValue(value)) shouldBe false
    }
  }

  it must "allow for sampling at the both ends" in {
    nodeEven.generateSampleAt(0) shouldBe IntValue(10)
    nodeEven.generateSampleAt(1) shouldBe IntValue(20)

    node1.generateSampleAt(0) shouldBe IntValue(1)
    node1.generateSampleAt(1) shouldBe IntValue(1)

    node2.generateSampleAt(0) shouldBe IntValue(1)
    node2.generateSampleAt(1) shouldBe IntValue(2)

    node3.generateSampleAt(0) shouldBe IntValue(1)
    node3.generateSampleAt(1) shouldBe IntValue(3)

    node4.generateSampleAt(0) shouldBe IntValue(1)
    node4.generateSampleAt(1) shouldBe IntValue(4)

    node5.generateSampleAt(0) shouldBe IntValue(1)
    node5.generateSampleAt(1) shouldBe IntValue(5)
  }

  it must "allow for sampling in the middle" in {
    node1.generateSampleAt(0.5) shouldBe IntValue(1)
    node2.generateSampleAt(0.5) shouldBe IntValue(2)
    node3.generateSampleAt(0.5) shouldBe IntValue(2)
    node4.generateSampleAt(0.5) shouldBe IntValue(3)
    node5.generateSampleAt(0.5) shouldBe IntValue(3)
  }

  it must "not allow to spilt an unit node" in {
    val unitNode = IntegerTree.empty(Range.inclusive(1, 1))
    unitNode.split shouldBe unitNode
  }

  it must "spilt node with two values - range" in {
    val node = IntegerTree.empty(Range.inclusive(10, 11))
    val left = IntegerTree.empty(Range.inclusive(10, 10))
    val right = IntegerTree.empty(Range.inclusive(11, 11))

    node.split shouldBe node.withChildren(List(left, right))
  }

  it must "spilt node with three values - range " in {
    val node = IntegerTree.empty(Range.inclusive(10, 12))
    val left = IntegerTree.empty(Range.inclusive(10, 10))
    val right = IntegerTree.empty(Range.inclusive(11, 12))

    node.split shouldBe node.withChildren(List(left, right))
  }

  it must "spilt node with four values - range" in {
    val node = IntegerTree.empty(Range.inclusive(10, 13))
    val left = IntegerTree.empty(Range.inclusive(10, 11))
    val right = IntegerTree.empty(Range.inclusive(12, 13))

    node.split shouldBe node.withChildren(List(left, right))
  }

  it must "spilt node with five values - range" in {
    val node = IntegerTree.empty(Range.inclusive(10, 14))
    val left = IntegerTree.empty(Range.inclusive(10, 11))
    val right = IntegerTree.empty(Range.inclusive(12, 14))

    node.split shouldBe node.withChildren(List(left, right))
  }

  val s11 = Sample(IntValue(1), 1)
  val s12 = Sample(IntValue(1), 2)
  val s21 = Sample(IntValue(2), 1)
  val s31 = Sample(IntValue(3), 1)

  it must "spilt node with two values - samples" in {
    val node = IntegerTree(List.empty, Range.inclusive(1, 2), Samples(s11, s21, s12))
    val left = IntegerTree(List.empty, Range.inclusive(1, 1), Samples(s11, s12))
    val right = IntegerTree(List.empty, Range.inclusive(2, 2), Samples(s21))

    node.split shouldBe node.withChildren(List(left, right))
  }

  it must "spilt node with three values - samples" in {
    val node = IntegerTree(List.empty, Range.inclusive(1, 3), Samples(s11, s21, s12, s31))
    val left = IntegerTree(List.empty, Range.inclusive(1, 1), Samples(s11, s12))
    val right = IntegerTree(List.empty, Range.inclusive(2, 3), Samples(s21, s31))

    node.split shouldBe node.withChildren(List(left, right))
  }

  it must "support adding new samples" in {
    val node = IntegerTree.empty(Range(1, 2)).addSample(1 -> 3.0)
    node.samples.size shouldBe 1
    node.samples.max.value shouldBe 3.0
  }

}
