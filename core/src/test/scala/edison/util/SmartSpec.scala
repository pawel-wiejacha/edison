package edison.util

import edison.search.{ IntValueImplicits, SampleImplicits }
import org.scalatest.{ FlatSpec, Matchers, OneInstancePerTest, OptionValues }

abstract class SmartSpec
    extends FlatSpec
    with Matchers
    with OneInstancePerTest
    with OptionValues
    with IntValueImplicits with SampleImplicits
    with TestHelpers {
}

