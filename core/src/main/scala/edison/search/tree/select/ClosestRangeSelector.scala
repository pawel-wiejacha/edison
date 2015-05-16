package edison.search.tree.select

import edison.search.Value
import edison.search.tree.Tree

object ClosestRangeSelector {
  def apply(targetValue: Value): Selector = new ClosestRangeSelector(targetValue)
}

private case class ClosestRangeOrdering(targetValue: Value) extends Selector.Ordering {
  override def compare(x: Tree, y: Tree): Int = {
    if (x.contains(targetValue)) -1
    else if (y.contains(targetValue)) 1
    else {
      assert(false, "Siblings should have disjoint ranges")
      0
    }
  }
}

/**
 * Selector that selects a leaf with (narrowest) range containing a given value.
 *
 * @param targetValue - value that is searched for
 */
class ClosestRangeSelector(targetValue: Value) extends Selector(new NoContext(ClosestRangeOrdering(targetValue))) {
  override def apply[T <: SelectionContext](root: Tree): Selection = {
    if (!root.contains(targetValue))
      throw new IllegalArgumentException(s"$root does not contain $targetValue")

    val result = super.apply(root)
    assert(result.tree.contains(targetValue))
    result
  }
}

