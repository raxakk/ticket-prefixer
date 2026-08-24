package io.github.raxakk.ticketprefixer

/**
 * Ready-made pairs of branch pattern and message template for the ticket schemes people
 * run into most often, so switching does not require writing a regular expression.
 *
 * A preset only fills the two fields on the settings page. They stay editable afterwards
 * and remain the single source of truth, so a preset is a starting point rather than a
 * mode the plugin runs in.
 */
enum class Preset(
    val label: String,
    val branchPattern: String,
    val messageTemplate: String,
    val example: String
) {

    /** Purely numeric ids, as used by Azure DevOps, GitLab and GitHub issues. */
    NUMERIC(
        label = "Numeric (default)",
        branchPattern = TicketExtractor.DEFAULT_BRANCH_PATTERN,
        messageTemplate = MessageTemplate.DEFAULT,
        example = "feature/12345678-name → #12345678: message"
    ),

    /**
     * Jira-style project keys.
     *
     * Upper case only, and deliberately so: a case-insensitive variant would read
     * `fix-1234` in `feature/fix-1234-login` as a project key. Teams whose branches carry
     * lower-case keys can drop `(?i)` in front of the pattern themselves.
     *
     * The template has no `#`, which would not belong in front of a Jira key.
     */
    JIRA(
        label = "Jira",
        branchPattern = """[A-Z][A-Z0-9]+-\d+""",
        messageTemplate = "{ticket}: {message}",
        example = "feature/PROJ-1234-name → PROJ-1234: message"
    );

    companion object {
        val DEFAULT: Preset = NUMERIC
    }
}
