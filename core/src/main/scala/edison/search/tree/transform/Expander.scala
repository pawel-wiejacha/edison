package edison.search.tree.transform

import edison.search.tree.{ Node, Leaf, Tree }

object Expander {
  type Predicate = Tree => Boolean
  def apply(expandPredicate: Expander.Predicate) = new Expander(expandPredicate)
}

/**
 * Expands tree by splitting leaves that match given predicate
 */
class Expander(expandPredicate: Expander.Predicate) {
  def apply(root: Tree): Tree = {
    def expandRec(tree: Tree): Tree = {
      tree match {
        case Node(_, _*) => tree.withChildren(tree.children.map(expandRec))
        case Leaf(_) if expandPredicate(tree) =>
          val newNode = tree.split
          if (newNode == tree) tree else expandRec(newNode)
        case _ => tree
      }
    }

    expandRec(root)
  }
}
