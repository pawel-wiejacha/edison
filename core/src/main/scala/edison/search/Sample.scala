package edison.search

import scala.language.implicitConversions

trait Value {
  def asInt: Int
}

case class IntValue(value: Int) extends Value {
  def asInt = value
}

case class Sample(value: Value, result: Double)

object IntValueImplicits {
  implicit def intToIntValue(value: Int): IntValue = IntValue(value)
}

object SampleImplicits {
  implicit def pairToSample[T](pair: (T, Result))(implicit conv: T => Value): Sample = Sample(pair._1, pair._2)
}
