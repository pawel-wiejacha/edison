package edison

package object util {

  trait Contra[-A] {}

  /** Can be used to define a union type. */
  type Union[A, B] = {
    type Check[Z] = Contra[Contra[Z]] <:< Contra[Contra[A] with Contra[B]]
  }
}
