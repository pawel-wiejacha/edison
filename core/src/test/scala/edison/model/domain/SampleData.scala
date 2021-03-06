package edison.model.domain

import edison.util.IntBytes.IntBytes

import scala.language.postfixOps

trait SampleData {

  object CacheEvictionPolicy extends Enumeration {
    val LRU, FIFO = Value
  }

  val paramDomain0_policy = ParamDomainEnum(CacheEvictionPolicy)
  val paramDomain1_cacheSize = ParamDomainInteger(Range.inclusive(4 MB, 200 MB, 1024))

  val paramDefs = ParamDefs(
    ParamDef("EvictionPolicy", paramDomain0_policy),
    ParamDef("CacheSize", paramDomain1_cacheSize)
  )

  val searchDomain = SearchDomain(paramDefs)

  val param0_fifo = paramDefs(0).createUnsafe(CacheEvictionPolicy.FIFO)
  val param0_lru = paramDefs(0).createUnsafe(CacheEvictionPolicy.LRU)
  val param1_5mb = paramDefs(1).createUnsafe(5 MB)
  val param1_8mb = paramDefs(1).createUnsafe(8 MB)

  val point1 = Point(searchDomain, Vector(param0_fifo, param1_5mb))
  val point2 = Point(searchDomain, Vector(param0_fifo, param1_8mb))
}
