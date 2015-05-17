package edison.util

trait TestHelpers {
  def fold[T](times: Int)(initial: T, f: T => T): T = {
    Range(0, times).foldLeft(initial)({ (acc, step) =>
      f(acc)
    })
  }
}

object TestHelpers extends TestHelpers
