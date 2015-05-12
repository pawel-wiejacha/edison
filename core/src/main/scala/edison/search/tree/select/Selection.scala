package edison.search.tree.select

import edison.search.tree.Tree

/**
 * Determines a subspace of the search domain.
 *
 * Currently it is quite useless because we are supporting only one-dimensional domains.
 */
case class Scope(range: Range)

case class Selection(tree: Tree, scope: Scope)
