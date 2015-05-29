package edison.yaml.project

class ParseError(val category: ErrorCategory, message: String) extends RuntimeException(message)

object ParseError {
  def apply(category: ErrorCategory, message: String): ParseError = {
    new ParseError(category, "ParseError: %s (%s)".format(category.errorName, message))
  }
}