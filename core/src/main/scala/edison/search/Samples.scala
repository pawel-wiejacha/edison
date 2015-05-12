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

  /**
   * Computes the Upper Confidence Bound.
   *
   * @param alpha tunable bias (exploitation vs exploration) parameter
   * @param parentVisitNum number of times the parent node was visited
   */
  def ucb(alpha: Double, parentVisitNum: Int): Option[Double] = {
    mean map { m =>
      // \mu_i  + \alpha * (ln(N) / n_i)^(1/2)
      m + alpha * Math.sqrt(Math.log(parentVisitNum) / size)
    }
  }

  def variance: Option[Result] = {
    mean map { m =>
      // VX = E(X^2) - EX^2
      results.map({ x => x * x }).sum / size - m * m
    }
  }
}
