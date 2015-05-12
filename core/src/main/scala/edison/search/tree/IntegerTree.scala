package edison.search.tree

import edison.search._

object IntegerTree {
  def empty(range: Range) = apply(children = List.empty, range, Samples.empty)
}

/** Search Tree that handles integer domains */
case class IntegerTree(children: List[Tree], range: Range, samples: Samples) extends Tree {
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
        IntegerTree(List.empty, leftRange, leftSamples),
        IntegerTree(List.empty, rightRange, rightSamples))

      withChildren(children)
    }
  }

  override def generateSampleAt(pos: Double): Value = IntValue(range(idxAt(pos)))

  override def contains(value: Value): Boolean = range.contains(value.asInt)

  private def idxAt(pos: Double): Int = {
    Math.min(range.size - 1, Math.floor(pos * range.size).toInt)
  }
}
