package edison.search.algorithm

import edison.search.tree.transform.{ Expander, Updater }
import edison.search.{ Sample, Value }
import edison.search.tree.Tree
import edison.search.tree.select.{ Selector, SelectionContext }
import edison.search.tree.select.Selector.Ordering

/**
 * UCT algorithm configuration.
 *
 * @param alpha - @see Samples.ucb
 * @param expandThreshold - number of samples needed to expand tree node
 */
case class UctConfig(alpha: Double, expandThreshold: Int)

/** UCT algorithm */
class UctAlgorithm(config: UctConfig) {

  /** Samples a value (search domain element) from a given tree. */
  def sample(root: Tree): Value = {
    val selector = Selector(UcbSelectionContext(root))
    val selection = selector(root)

    selection.tree.generateSample
  }

  /**
   * Adds sample to a given tree.
   *
   * It does not expand tree automatically.
   */
  def update(root: Tree, sample: Sample): Tree = {
    val predicate: Updater.Predicate = tree => tree.contains(sample.value)
    val transformation: Updater.Transformation = tree => tree.addSample(sample)
    val updater = Updater(predicate, transformation)

    updater(root)
  }

  /** Expands tree that was previously updated. */
  def expand(root: Tree): Tree = {
    val expander = Expander(tree => tree.samples.size >= config.expandThreshold)
    expander(root)
  }

  /** Selects nodes with the highest UCB */
  private[algorithm] case class UcbSelectionContext(parentNode: Tree) extends SelectionContext {

    override def update(selectedNode: Tree): SelectionContext = copy(parentNode = selectedNode)

    override def getOrdering: Ordering = new Ordering {
      override def compare(x: Tree, y: Tree): Int = {
        val xUcb = x.samples.ucb(config.alpha, parentNode.samples.size)
        val yUcb = y.samples.ucb(config.alpha, parentNode.samples.size)

        (xUcb, yUcb) match {
          case (None, None) => 0
          case (_, None) => 1
          case (None, _) => -1
          case _ => Ordering[Double].compare(yUcb.get, xUcb.get)
        }
      }
    }
  }

}
