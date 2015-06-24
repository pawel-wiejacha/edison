package edison.model.domain

import edison.util.IntBytes.IntBytes
import edison.util.SmartFreeSpec

import scala.language.postfixOps

class SearchDomainModelTest extends SmartFreeSpec with SampleData {

  "IntegerParam" - {
    "can hold value conforming domain constraints" in {
      IntegerParam(4 MB, paramDomain1_cacheSize).value shouldBe (4 MB)
      IntegerParam(4.MB + 1024, paramDomain1_cacheSize).value shouldBe (4.MB + 1024)
    }

    "must check the domain it belongs to" in {
      intercept[AssertionError] { IntegerParam(4.MB - 1, paramDomain1_cacheSize) }
      intercept[AssertionError] { IntegerParam(4.MB + 1025, paramDomain1_cacheSize) }
    }
  }

  "ParamDomainInteger" - {
    "can create IntegerParams" in {
      paramDomain1_cacheSize.create(4 MB).domain shouldBe paramDomain1_cacheSize
      paramDomain1_cacheSize.create(4 MB).value shouldBe (4 MB)
    }

    "should override ParamDomain methods" in {
      paramDomain1_cacheSize.isDiscrete shouldBe true
      paramDomain1_cacheSize.isNumeric shouldBe true
      paramDomain1_cacheSize.isContinuous shouldBe false
    }
  }

  "ParamDomainEnum" - {
    "can create EnumParams" in {
      val param = paramDomain0_policy.create(CacheEvictionPolicy.FIFO)
      param.value shouldBe CacheEvictionPolicy.FIFO
      param.domain shouldBe paramDomain0_policy
    }

    "can createUnsafe EnumParams (from enumeration)" in {
      val param = paramDomain0_policy.createUnsafe(CacheEvictionPolicy.FIFO)
      param.value shouldBe CacheEvictionPolicy.FIFO
      param.domain shouldBe paramDomain0_policy
    }

    "can createUnsafe EnumParams (from String)" in {
      val param = paramDomain0_policy.createUnsafe("FIFO")
      param.value shouldBe CacheEvictionPolicy.FIFO
      param.domain shouldBe paramDomain0_policy
    }

    "does some checking during createUnsafe" in {
      intercept[NoSuchElementException] { paramDomain0_policy.createUnsafe("invalid") }
    }

    "should override ParamDomain methods" in {
      paramDomain0_policy.isDiscrete shouldBe true
      paramDomain0_policy.isNumeric shouldBe false
      paramDomain0_policy.isContinuous shouldBe false
    }
  }

  "ParamDefs" - {
    "can have its parameter definitions" - {
      "accessed by index" in {
        searchDomain.paramDefs(0).domain shouldBe paramDomain0_policy
        searchDomain.paramDefs(1).domain shouldBe paramDomain1_cacheSize
      }
      "accessed by name" in {
        searchDomain.paramDefs("EvictionPolicy").domain shouldBe paramDomain0_policy
        searchDomain.paramDefs("CacheSize").domain shouldBe paramDomain1_cacheSize
      }
    }
  }

  "Point" - {
    "can have its coordinates accessed easily" in {
      point1.params(0).value.value shouldBe CacheEvictionPolicy.FIFO
      point1.params(0).domain shouldBe paramDomain0_policy
      point1.params(0).name shouldBe "EvictionPolicy"

      point1.params(1).value.value shouldBe (5 MB)
      point1.params(1).domain shouldBe paramDomain1_cacheSize
      point1.params(1).name shouldBe "CacheSize"
    }

    "must have comparison operator defined correctly" in {
      (point1 == point2) shouldBe false
      (point1 == point1) shouldBe true

      val pointLikePoint1 = {
        val coord0 = searchDomain.paramDefs(0).createUnsafe(CacheEvictionPolicy.FIFO)
        val coord1 = searchDomain.paramDefs(1).createUnsafe(5 MB)
        Point(searchDomain, Vector(coord0, coord1))
      }
      (point1 == pointLikePoint1) shouldBe true
    }
  }

}
