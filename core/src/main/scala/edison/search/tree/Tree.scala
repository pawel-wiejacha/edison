package edison.search.tree

import edison.search.{Sample, Samples, Value}

import scala.util.Random

/** UCTS/MCTS Search Tree (base trait) */
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
    assert(contains(sample.value))
    updated(samples = samples.add(sample))
  }

  def generateSample: Value = generateSampleAt(Random.nextFloat())
}

object Node {
  def unapply(tree: Tree): Option[List[Tree]] = if (tree.children.isEmpty) None else Some(tree.children)
}

object Leaf {
  def unapply(tree: Tree): Boolean = tree.children.isEmpty
}
