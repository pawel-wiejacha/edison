package edison.search.tree.select

import edison.search.tree.Tree

/** Simple selector with static (context-free) ordering */
object StaticSelector {
  def apply(ordering: Selector.Ordering) = new Selector(new NoContext(ordering))
}

class NoContext(ordering: Selector.Ordering) extends SelectionContext {
  override def update(selectedNode: Tree): SelectionContext = this
  override def getOrdering: Selector.Ordering = ordering
}
