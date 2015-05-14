package edison.search.tree

import edison.search.{ Sample, Samples, Value }

import scala.util.Random

/** UCT/MCTS Search Tree (base trait) */
trait Tree {
  def samples: Samples
  def children: List[Tree]

  /** @return may return unchanged tree if split was not possible */
  def split: Tree
  def generateSampleAt(range: Double): Value
  def contains(value: Value): Boolean
  def updated(samples: Samples): Tree
  def withChildren(children: List[Tree]): Tree

  def addSample(sample: Sample): Tree = {
    assert(contains(sample.value), s"$this cannot contain $sample")
    updated(samples = samples.add(sample))
  }

  def generateSample: Value = generateSampleAt(Random.nextFloat())
}

/**
 * Search domain subspace.
 *
 * It's used only for pattern matching.
 */
trait Subspace

object Node {
  def unapplySeq(tree: Tree): Option[(Subspace, List[Tree])] = {
    tree match {
      case IntegerTree(range, children, _) if children.nonEmpty => Some((IRange(range), children))
      case _ => None
    }
  }
}

object Leaf {
  def unapply(tree: Tree): Option[Subspace] = {
    tree match {
      case IntegerTree(range, Nil, _) => Some(IRange(range))
      case _ => None
    }
  }
}
