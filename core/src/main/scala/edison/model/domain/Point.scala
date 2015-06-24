package edison.model.domain

import edison.model._

/**
 * An element of a search space - described by its coordinates (params).
 *
 * @param domain the search domain this point belongs to
 * @param params coordinates that describe the location of this point in the space defined by the search domain
 */
case class Point(domain: SearchDomain, params: Params)
