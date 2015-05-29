package edison.yaml.project

import edison.util.ClassUtil

/**
 * I/O and syntax errors.
 *
 * @see DocumentStructure for structural (domain-specific) errors.
 */
object Errors {
  object IOError extends ParserErrorCategory
  object YamlError extends ParserErrorCategory
  object UnknownError extends ParserErrorCategory
}

trait ParserErrorCategory extends ErrorCategory {
  override def errorName: String = "Parser." + ClassUtil.getScalaClassName(getClass, Errors.getClass)
}
