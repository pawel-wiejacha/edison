package edison

import edison.model.domain.Param

package object model {
  /** Name of the parameter (Param) and parameter definition (ParamDef). */
  type ParamName = String

  /** Describes the coordinates of a Point instances. */
  type Params = Vector[Param]
}
