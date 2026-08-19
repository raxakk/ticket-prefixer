package io.github.raxakk.ticketstamp

/**
 * Extracts the ticket number from a Git branch name using a user-supplied regex.
 *
 * If the pattern declares a capturing group, the first non-empty group is used as the
 * ticket; otherwise the whole match is. That way both `(\d{4,})` and `\d{4,}` work.
 */
object TicketExtractor {

    /**
     * Matches a run of at least four digits that forms a complete segment of the branch
     * name, so both `feature/123456789-name` and `feature/123456789/name` yield
     * `123456789`. The minimum length keeps segments like `v2` or `fix-3` from matching.
     */
    const val DEFAULT_BRANCH_PATTERN: String = """(?:^|[/\-_])(\d{4,})(?=[/\-_]|$)"""

    fun isValidPattern(pattern: String): Boolean = compile(pattern) != null

    /** Returns `null` if the pattern is invalid or does not match. */
    fun extract(branchName: String, pattern: String): String? {
        val match = compile(pattern)?.find(branchName) ?: return null
        return match.groupValues.drop(1).firstOrNull { it.isNotEmpty() } ?: match.value
    }

    private fun compile(pattern: String): Regex? =
        runCatching { Regex(pattern) }.getOrNull()
}
