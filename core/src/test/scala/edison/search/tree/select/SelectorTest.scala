package edison.search.tree.select

import edison.search.tree.Tree
import edison.search.tree.transform.TestTree
import edison.util.SmartSpec
import org.scalatest.Assertions

class SelectorTest extends SmartSpec with TestTree with Assertions {

  val moreSamplesOrdering = new Selector.Ordering {
    override def compare(x: Tree, y: Tree): Int = Ordering[Int].compare(y.samples.size, x.samples.size)
  }

  val selector = Selector(moreSamplesOrdering)

  behavior of "Selector"

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

}
