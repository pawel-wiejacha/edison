package edison.util

import edison.search.{ IntValueImplicits, SampleImplicits }
import org.scalatest._

trait AbstractSmartSpec
    extends Matchers
    with OneInstancePerTest
    with OptionValues
    with TryValues
    with IntValueImplicits with SampleImplicits
    with TestHelpers { this: Suite =>

  implicit class StringTrimmer(str: String) {
    def strip: String = {
      str.stripMargin.trim
    }
  }
}

abstract class SmartSpec extends FlatSpec with AbstractSmartSpec
abstract class SmartFreeSpec extends FreeSpec with AbstractSmartSpec

