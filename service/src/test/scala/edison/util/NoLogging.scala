package edison.util

import com.typesafe.scalalogging.{ Logger, StrictLogging }
import org.slf4j.helpers.NOPLogger

trait NoLogging { this: StrictLogging =>
  override val logger = Logger(new NOPLogger {})
}
