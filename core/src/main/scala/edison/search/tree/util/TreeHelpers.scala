package edison.search.tree.util

import edison.search.tree.Tree

object TreeHelpers {

  /** Traverses tree in pre-order way */
  def foreach(root: Tree)(f: Tree => Unit): Unit = {
    def visitRec(tree: Tree): Unit = {
      f(tree)
      tree.children.foreach(visitRec)
    }

    visitRec(root)
  }

}
