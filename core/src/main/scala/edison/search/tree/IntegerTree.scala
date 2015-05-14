package edison.search.tree

import edison.search._

object IntegerTree {
  def empty(range: Range) = apply(range, children = List.empty, Samples.empty)
}

/** Search Tree that handles integer domains */
case class IntegerTree(range: Range, children: List[Tree], samples: Samples = Samples.empty) extends Tree {
  assert(range.size > 0)

  override def updated(newSamples: Samples): IntegerTree = copy(samples = newSamples)
  override def withChildren(children: List[Tree]): Tree = copy(children = children)

  override def split: Tree = {
    assert(children.isEmpty)

    if (range.size <= 1) {
      this
    } else {
      val leftRange = Range(range.start, range(idxAt(0.5)), range.step)
      val rightRange = Range.inclusive(range(idxAt(0.5)), range.end, range.step)
      val leftSamples = Samples(samples.values.filter({ x => leftRange.contains(x.value.asInt) }))
      val rightSamples = Samples(samples.values.filter({ x => rightRange.contains(x.value.asInt) }))
      val children = List(
        IntegerTree(leftRange, List.empty, leftSamples),
        IntegerTree(rightRange, List.empty, rightSamples)
      )

      withChildren(children)
    }
  }

  override def generateSampleAt(pos: Double): Value = IntValue(range(idxAt(pos)))

  override def contains(value: Value): Boolean = range.contains(value.asInt)

  private def idxAt(pos: Double): Int = {
    Math.min(range.size - 1, Math.floor(pos * range.size).toInt)
  }
}

/**
 * Inclusive range.
 *
 * It's used only for pattern matching (because Range does not have `unapply` defined)
 */
case class IRange(start: Int, end: Int) extends Subspace

object IRange {
  def apply(range: Range): IRange = {
    assert(range.nonEmpty)

    if (range.isInclusive)
      IRange(range.start, range.end)
    else
      IRange(range.start, range.last)
  }
}
