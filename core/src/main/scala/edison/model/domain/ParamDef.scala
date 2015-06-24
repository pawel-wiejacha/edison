package edison.model.domain

import edison.model._

/**
 * Parameter definition is a search domain component definition.
 *
 * Search domain is a cartesian product of multiple parameter domains, where each parameter domain is described by a ParamDef.
 *
 * Example:
 *
 * If the search domain was a product two parameters:
 *   - cache size - any integer larger than 32 MB and smaller than 4 GB
 *   - cache eviction policy - one of: FIFO, LeastRecentlyUsed, SecondChance
 *
 * Then the search domain parameter definitions would be:
 *   - ParamDef("cacheSize", ParamDomainInteger(Range(32 MB, 4 GB)))
 *   - ParamDef("evictionPolicy", ParamDomainEnum("FIFO", "LRU", "SecondChance"))
 */
case class ParamDef(name: ParamName, domain: ParamDomain) {
  def create(value: domain.Value): Param = Param(name, domain.create(value))
  def createUnsafe[T](value: T): Param = Param(name, domain.createUnsafe(value.asInstanceOf[domain.Value]))
}
