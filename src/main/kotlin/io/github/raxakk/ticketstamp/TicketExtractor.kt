package io.github.raxakk.ticketstamp

/**
 * Extracts the ticket number from a Git branch name.
 *
 * The number has to form a complete segment of the branch name, delimited by
 * `/`, `-`, `_` or the start/end of the name — so both `feature/123456789-name`
 * and `feature/123456789/name` yield `123456789`.
 */
object TicketExtractor {

    /**
     * Requiring at least four digits keeps segments like `v2` or `fix-3` from being
     * mistaken for a ticket number.
     */
    private val TICKET_REGEX = Regex("""(?:^|[/\-_])(\d{4,})(?=[/\-_]|$)""")

    fun extract(branchName: String): String? =
        TICKET_REGEX.find(branchName)?.groupValues?.get(1)
}
