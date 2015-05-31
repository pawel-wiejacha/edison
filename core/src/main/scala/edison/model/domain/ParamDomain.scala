package edison.model.domain

/**
 * Defines the domain of a single search space coordinate.
 *
 * @see ParamDef
 */
sealed trait ParamDomain {
  type Value

  def isDiscrete: Boolean
  def isNumeric: Boolean
  def isContinuous: Boolean = isNumeric && !isDiscrete

  def create(value: Value): ParamValue
  def createUnsafe[T](value: T): ParamValue = create(value.asInstanceOf[Value])
}

/**
 * ParamDomain that can describe discrete, non-numeric parameters (e.g. enum, factor, categorical variable, boolean).
 */
case class ParamDomainEnum[T <: Enumeration](enumeration: T) extends ParamDomain {
  type Value = enumeration.Value

  override def isDiscrete: Boolean = true
  override def isNumeric: Boolean = false

  def create(value: Value): EnumParam[T, this.type] = EnumParam(value, this)
}

/**
 * ParamDomain that can describe integer parameters.
 */
case class ParamDomainInteger(range: Range) extends ParamDomain {
  type Value = Int

  override def isDiscrete: Boolean = true
  override def isNumeric: Boolean = true

  def create(value: Value): IntegerParam[this.type] = IntegerParam(value, this)
}
