package edison.search

object Samples {
  def apply(values: Sample*) = new Samples(List(values: _*))
  def empty: Samples = Samples(List.empty)
}

/** Structure that holds [tree] search samples and their summary (mean, sd, ucb, etc.) */
case class Samples(values: List[Sample]) {
  val results: List[Result] = values.map({ value => value.result })

  def add(sample: Sample): Samples = {
    copy(values = sample :: values)
  }

  /** Number of samples */
  def size: Int = values.size
  def mean: Option[Result] = results.reduceOption(_ + _).map(_ / size)
  def sd: Option[Result] = variance.map(Math.sqrt)
  def max: Option[Result] = results.reduceOption(_ max _)
  def min: Option[Result] = results.reduceOption(_ min _)

  def variance: Option[Result] = {
    mean map { m =>
      // VX = E(X^2) - EX^2
      results.map({ x => x * x }).sum / size - m * m
    }
  }
}
