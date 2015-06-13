package edison.model.domain

import scala.language.existentials

/** An actual value (e.g. 42) of a Param */
sealed trait ParamValue {
  type DomainType <: ParamDomain

  def domain: DomainType
  def value: DomainType#Value
}

case class IntegerParam[T <: ParamDomainInteger](value: Int, domain: T) extends ParamValue {
  type DomainType = T

  assert(domain.range.contains(value), s"$value not in ${domain.range}")
}

case class EnumParam[K <: Enumeration, T <: ParamDomainEnum[K]](value: T#Value, domain: T) extends ParamValue {
  type DomainType = T

  assert(
    domain.enumeration.values.contains(value.asInstanceOf[domain.enumeration.Value]),
    s"$value not in ${domain.enumeration.values}"
  )
}
