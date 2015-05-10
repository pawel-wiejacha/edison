package edison.search

import scala.util.Random

/** UCTS/MCTS Search Tree (base trait) */
trait Tree {
  def samples: Samples
  def children: List[Tree]

  /** @return list of children or an empty list if split was not possible */
  def split: List[Tree]
  def generateSampleAt(range: Double): Value
  def contains(value: Value): Boolean
  def updated(samples: Samples): Tree

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
