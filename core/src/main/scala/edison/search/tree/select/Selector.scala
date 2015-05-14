package edison.search.tree.select

import edison.search.tree.{ IntegerTree, Leaf, Node, Tree }
import scala.annotation.tailrec

object Selector {
  type Ordering = scala.math.Ordering[Tree]

  def apply(initialContext: SelectionContext): Selector = new Selector(initialContext)
}

/**
 * Selection context (state, accumulator) that can be used to create dynamic orderings.
 *
 * The Selector updates the context after selecting the best node on each tree level.
 * Context wraps the selection state and allows to create dynamic orderings (e.g. that depends on the previously visited nodes).
 */
trait SelectionContext {
  def update(selectedNode: Tree): SelectionContext
  def getOrdering: Selector.Ordering
}

/**
 * Goes down the tree to select the best (in the `ordering` terms) leaf and the most promising search subspace.
 *
 * @see SelectionContext
 */
class Selector(initialContext: SelectionContext) {
  def apply[T <: SelectionContext](root: Tree): Selection = {
    @tailrec def selectRec(selection: Selection, context: SelectionContext): Selection = {
      selection.tree match {
        case Node(_, children @ _*) =>
          val ordering = context.getOrdering
          val bestNode = children.sorted(ordering).head
          val newContext = context.update(bestNode)
          selectRec(Selection(bestNode, getScope(bestNode)), newContext)
        case Leaf(_) => selection
      }
    }

    selectRec(Selection(root, getScope(root)), initialContext)
  }

  private def getScope(tree: Tree): Scope = {
    tree match {
      case t: IntegerTree => Scope(t.range)
    }
  }
}
