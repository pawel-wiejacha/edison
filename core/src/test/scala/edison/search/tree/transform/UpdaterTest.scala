package edison.search.tree.transform

import edison.search.{ IntValue, Sample }
import edison.util.SmartSpec

class UpdaterTest extends SmartSpec with TestTree {

  val newSample = Sample(IntValue(250), 444.0)
  def addSample: Updater.Transformation = tree => tree.addSample(newSample).withChildren(Nil)

  behavior of "Tree Updater"

  it must "ignore leaves and nodes that do not match the predicate" in {
    Updater(x => false, addSample)(n2) shouldBe n2
    Updater(x => false, addSample)(n1) shouldBe n1
  }

  it must "transform leaves that match the predicate" in {
    Updater(x => x == n2, addSample)(n2) shouldBe n2.addSample(newSample)
  }

  it must "transform nodes that match the predicate" in {
    Updater(x => x == n1, addSample)(n1) shouldBe n1.addSample(newSample)
  }

  it must "transform nodes recursively and propagate changes up" in {
    val updatedN1 = n1.withChildren(List(n2.addSample(newSample), n3))

    Updater(tree => tree == n2, addSample)(n1) shouldBe updatedN1
  }

}
