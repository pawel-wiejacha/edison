package edison.util

class UnionTypeTest extends SmartSpec {

  behavior of "Union[A, B]"

  it can "be used to define function that takes a union type" in {
    def getType[T: Union[Int, String]#Check](t: T) = t match {
      case _: Int => "Int"
      case _: String => "String"
    }

    getType(123) shouldBe "Int"
    getType("abc") shouldBe "String"
    // getType(3.14) will not compile
  }

}
