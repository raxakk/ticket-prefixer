package io.github.raxakk.ticketstamp

/**
 * Renders the template that controls how the ticket is written into the commit message.
 *
 * `{ticket}` is replaced by the extracted number and `{message}` marks where the text
 * already in the field goes — so `#{ticket}: {message}` prepends, `{message} (#{ticket})`
 * appends, and anything in between works too.
 */
object MessageTemplate {

    const val DEFAULT: String = "#{ticket}: {message}"

    const val TICKET_PLACEHOLDER: String = "{ticket}"
    const val MESSAGE_PLACEHOLDER: String = "{message}"

    /**
     * Splits the rendered template into the parts that go before and after the existing
     * commit message.
     *
     * A template without [MESSAGE_PLACEHOLDER] is treated as a plain prefix, i.e.
     * `#{ticket}:` behaves like `#{ticket}: {message}`.
     */
    fun render(template: String, ticket: String): Pair<String, String> {
        val withTicket = template.replace(TICKET_PLACEHOLDER, ticket)
        val normalized =
            if (withTicket.contains(MESSAGE_PLACEHOLDER)) withTicket
            else "$withTicket $MESSAGE_PLACEHOLDER"

        return normalized.substringBefore(MESSAGE_PLACEHOLDER) to
            normalized.substringAfter(MESSAGE_PLACEHOLDER)
    }
}
