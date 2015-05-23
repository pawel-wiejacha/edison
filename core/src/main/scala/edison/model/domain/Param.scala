package edison.model.domain

import edison.model._

/**
 * An element of a ParamDef domain - single coordinate in the search domain space.
 *
 * For example, `Param("cacheSize", 60 mb)` belongs to `ParamDef("cacheSize", ParamDomainInteger(Range(32 mb, 4 gb)))`.
 *
 * @param name - name of the ParamDef this element belongs to
 */
case class Param(name: ParamName, value: ParamValue) {
  def domain: ParamDomain = value.domain
}
