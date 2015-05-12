package edison.util

import edison.search.{ SampleImplicits, IntValueImplicits }
import org.scalatest.{ OptionValues, OneInstancePerTest, FlatSpec, Matchers }

abstract class SmartSpec
    extends FlatSpec
    with Matchers
    with OneInstancePerTest
    with OptionValues
    with IntValueImplicits with SampleImplicits {
}

