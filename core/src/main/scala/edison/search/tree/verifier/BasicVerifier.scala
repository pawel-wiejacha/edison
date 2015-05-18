package edison.search.tree.verifier

import edison.search.{ Sample, Result, Value }
import edison.search.tree.{ IntegerTree, Leaf, Node, Tree }
import edison.search.tree.util.TreeHelpers

abstract class TreeVerifier(root: Tree) {
  def verify: Unit

  protected def expect(expectation: String)(block: => Boolean): Unit = {
    assert(block, s"Tree verification failed. Expectation: '$expectation'. Tree=${root}")
  }
}

class BasicVerifier(root: Tree) extends TreeVerifier(root) {

  def verify: Unit = {
    TreeHelpers.foreach(root)({
      case tree @ Node(_, _*) => verifyNode(tree)
      case Leaf(_) =>
    })
  }

  private def verifyNode(node: Tree): Unit = {
    val parentRange = node.asInstanceOf[IntegerTree].range
    val childrenRanges = node.children.map(_.asInstanceOf[IntegerTree].range)

    expect("Parent samples must be distributed among its children") {
      val childrenSamples = node.children.map(_.samples).reduce(_ ::: _)
      val asPair = { sample: Sample => (sample.value.asInt, sample.result) }
      node.samples.values.map(asPair).sorted == childrenSamples.values.map(asPair).sorted
    }

    expect("Set of children subdomains must be a partition of parent subdomain") {
      case class Acc(allAdjacent: Boolean, expectedStart: Int)

      val rangesAreAdjacent = childrenRanges.foldLeft(Acc(true, parentRange.start))({ (acc, childRange) =>
        val nextStart = childRange.last + parentRange.step
        Acc(acc.allAdjacent && acc.expectedStart == childRange.start, nextStart)
      }).allAdjacent

      val rightEndsAreTheSame = childrenRanges.last.end == parentRange.end &&
        childrenRanges.last.isInclusive == parentRange.isInclusive

      val stepsAreTheSame = childrenRanges.forall(_.step == parentRange.step)

      rangesAreAdjacent && rightEndsAreTheSame && stepsAreTheSame
    }

    expect("Child subdomain must be smaller than the parent one") {
      childrenRanges.forall({ childRange => childRange.size < parentRange.size })
    }
  }
}
