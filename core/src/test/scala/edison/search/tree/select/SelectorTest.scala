package edison.search.tree.select

import edison.search.tree.Tree
import edison.search.tree.transform.TestTree
import edison.util.SmartSpec
import org.scalatest.Assertions

class SelectorTest extends SmartSpec with TestTree with Assertions {

  val moreSamplesOrdering = new Selector.Ordering {
    override def compare(x: Tree, y: Tree): Int = Ordering[Int].compare(y.samples.size, x.samples.size)
  }

  val selector = StaticSelector(moreSamplesOrdering)

  behavior of "StaticSelector"

  it must "update the scope and return leaf when applied to a leaf" in {
    selector(n5) shouldBe Selection(n5, Scope(n5.range))
  }

  it must "use ordering to choose the best child" in {
    val n4Updated = n4.addSample(400 -> 5.0)
    val n5Updated = n5.addSample(500 -> 5.0)

    val `n3 with n4 updated` = n3.withChildren(List(n4Updated, n5))
    val `n3 with n5 updated` = n3.withChildren(List(n4, n5Updated))

    selector(`n3 with n4 updated`) shouldBe Selection(n4Updated, Scope(n4.range))
    selector(`n3 with n5 updated`) shouldBe Selection(n5Updated, Scope(n5.range))
  }

  it must "use stable sort" in {
    selector(n3) shouldBe Selection(n4, Scope(n4.range))
  }

  it must "traverse tree recursively" in {
    val `n1 with n3 updated` = n1.withChildren(List(n2, n3.addSample(400 -> 5.0)))
    selector(`n1 with n3 updated`) shouldBe Selection(n4, Scope(n4.range))
  }

  behavior of "Selector"

  it can "use custom SelectionContext to create dynamic orderings" in {

    def fixedMinElementOrdering(minElement: Tree) = new Selector.Ordering {
      override def compare(x: Tree, y: Tree): Int =
        if (x == minElement) -1
        else if (y == minElement) 1
        else 0
    }

    case class CustomSelectionContext(expectedLeaf: Tree, depth: Int) extends SelectionContext {
      override def update(selectedNode: Tree): CustomSelectionContext = {
        assert(depth < 2)
        copy(depth = depth + 1)
      }

      override def getOrdering: Selector.Ordering = {
        depth match {
          case 0 => fixedMinElementOrdering(n3)
          case 1 => fixedMinElementOrdering(expectedLeaf)
        }
      }
    }

    Selector(new CustomSelectionContext(n5, 0))(n1) shouldBe Selection(n5, Scope(n5.range))
    Selector(new CustomSelectionContext(n4, 0))(n1) shouldBe Selection(n4, Scope(n4.range))
  }

  behavior of "ClosestRangeSelector"

  it must "traverse the tree in order to find closest match" in {
    ClosestRangeSelector(200)(n1).tree shouldBe n2
    ClosestRangeSelector(250)(n1).tree shouldBe n2
    ClosestRangeSelector(299)(n1).tree shouldBe n2

    ClosestRangeSelector(300)(n1).tree shouldBe n4
    ClosestRangeSelector(400)(n1).tree shouldBe n4
    ClosestRangeSelector(499)(n1).tree shouldBe n4

    ClosestRangeSelector(500)(n1).tree shouldBe n5
    ClosestRangeSelector(550)(n1).tree shouldBe n5
    ClosestRangeSelector(599)(n1).tree shouldBe n5
  }

  it must "throw IllegalArgumentException when appropriate node could not be found" in {
    intercept[IllegalArgumentException] { ClosestRangeSelector(199)(n1).tree }
    intercept[IllegalArgumentException] { ClosestRangeSelector(600)(n1).tree }
    intercept[IllegalArgumentException] { ClosestRangeSelector(900)(n1).tree }
  }

}
