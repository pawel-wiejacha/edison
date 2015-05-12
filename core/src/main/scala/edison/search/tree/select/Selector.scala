package edison.search.tree.select

import edison.search.tree.{ IntegerTree, Leaf, Node, Tree }
import scala.annotation.tailrec

object Selector {
  type Ordering = scala.math.Ordering[Tree]

  def apply(ordering: Ordering) = new Selector(ordering)
}

/**
 * Goes down the tree to select the best (in the `ordering` terms) leaf and the most promising search subspace.
 */
class Selector(ordering: Selector.Ordering) {
  def apply(root: Tree): Selection = {
    @tailrec def selectRec(selection: Selection): Selection = {
      selection.tree match {
        case Node(children) =>
          val bestNode = children.sorted(ordering).head
          selectRec(Selection(bestNode, getScope(bestNode)))
        case Leaf() => selection
      }
    }

    selectRec(Selection(root, getScope(root)))
  }

  private def getScope(tree: Tree): Scope = {
    tree match {
      case t: IntegerTree => Scope(t.range)
    }
  }
}
