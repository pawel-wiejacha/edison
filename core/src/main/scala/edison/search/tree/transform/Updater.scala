package edison.search.tree.transform

import edison.search.tree.{ Node, Leaf, Tree }

object Updater {
  type Predicate = Tree => Boolean

  /** Transformation should create a tree without children, because they will be replaced anyway */
  type Transformation = Tree => Tree

  def apply(predicate: Updater.Predicate, transform: Updater.Transformation) =
    new Updater(predicate, transform)
}

/**
 * Updates tree by replacing (using `transform` function) nodes that match given predicate.
 */
class Updater(predicate: Updater.Predicate, transform: Updater.Transformation) {
  def apply(root: Tree): Tree = {
    def updateRec(tree: Tree): Tree = {
      val newNode = if (predicate(tree)) transform(tree) else tree

      tree match {
        case Node(_, _*) => newNode.withChildren(tree.children.map(updateRec))
        case Leaf(_) => newNode
      }
    }

    updateRec(root)
  }
}
