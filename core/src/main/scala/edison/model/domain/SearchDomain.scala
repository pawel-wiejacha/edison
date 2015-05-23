package edison.model.domain

/**
 * SearchDomain defines the search space of parameters to be explored.
 *
 * Search domain is a cartesian product of multiple parameter domains, where each parameter domain is described by a ParamDef.
 *
 * @param paramDefs define the search domain
 *
 * @see ParamDef
 *
 */
case class SearchDomain(paramDefs: ParamDefs)
