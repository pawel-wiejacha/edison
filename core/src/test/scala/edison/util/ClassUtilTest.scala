package edison.util

class ClassUtilTest extends SmartSpec {

  object SampleObject {
    class SampleClass
  }

  val sampleClass = new SampleObject.SampleClass

  behavior of "ClassUtil"

  it must "extract Scala class names" in {
    ClassUtil.getScalaClassName(sampleClass.getClass, classOf[ClassUtilTest]) shouldBe "SampleObject.SampleClass"
    ClassUtil.getSimpleScalaClassName(sampleClass.getClass) shouldBe "SampleClass"
  }

  it must "extract anonymous Scala class names" in {
    val anonObj = new {
      object NestedObject {
        class NestedClass
      }
      def foo() = new NestedObject.NestedClass
    }

    import scala.language.reflectiveCalls
    ClassUtil.getScalaClassName(anonObj.foo.getClass, anonObj.getClass) shouldBe "NestedObject.NestedClass"
  }

  it must "assert when invalid prefix is provided" in {
    intercept[AssertionError] { ClassUtil.getScalaClassName(sampleClass.getClass, classOf[String]) }
  }

}
