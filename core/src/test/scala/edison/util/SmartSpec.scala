package edison.util

import org.scalatest.{OptionValues, OneInstancePerTest, FlatSpec, Matchers}

abstract class SmartSpec extends FlatSpec with Matchers with OneInstancePerTest with OptionValues {
}

