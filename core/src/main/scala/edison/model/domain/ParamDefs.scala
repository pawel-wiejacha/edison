package edison.model.domain

import edison.model._

object ParamDefs {
  def apply(params: Seq[ParamDef]): ParamDefs = {
    new ParamDefs(params.toVector, Map[ParamName, ParamDef](params.map({ p => (p.name, p) }): _*))
  }
}

/**
 * An ordered set of ParamDefs that allows for accessing parameter definitions by name and by index.
 */
case class ParamDefs private (list: Vector[ParamDef], map: Map[ParamName, ParamDef]) {
  def apply(idx: Int): ParamDef = list(idx)
  def apply(paramName: ParamName): ParamDef = map(paramName)
}
