package io.github.raxakk.ticketprefixer

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

    /**
     * Characters that may decorate a ticket reference on its left, e.g. the `#` in
     * `#4711` or the `[` in `[4711]`.
     */
    private const val LEADING_DECORATION: String = " \t#([{<"

    /**
     * Characters that may separate a leading ticket reference from the message after it,
     * e.g. the `: ` in `4711: fix login` or the `] ` in `[4711] fix login`. Opening
     * brackets are deliberately absent so that `[4711] [WIP] fix` keeps its second tag.
     */
    private const val TRAILING_SEPARATORS: String = " \t:;,.-\u2013\u2014|/)]}>"

    /**
     * Length of the reference to [ticket] that [message] opens with, decoration included,
     * or `0` when it does not open with one.
     *
     * `4711: fix`, `[4711] fix` and `#4711 fix` all report the length up to `fix`, so the
     * caller can swap that run for its own rendering. A mention further inside the text,
     * as in `as described in 4711`, reports `0` — rewriting that would mean dragging the
     * number to the front and mangling the sentence.
     */
    fun leadingTicketReferenceLength(message: String, ticket: String): Int {
        if (ticket.isEmpty()) return 0

        var start = 0
        while (start < message.length && message[start] in LEADING_DECORATION) start++
        if (!message.startsWith(ticket, start)) return 0

        var end = start + ticket.length
        // `4711` must not be read out of `47110`.
        if (end < message.length && message[end].isLetterOrDigit()) return 0
        while (end < message.length && message[end] in TRAILING_SEPARATORS) end++

        return end
    }

    /**
     * Reports whether [message] already refers to [ticket], in whatever format.
     *
     * Checking for the rendered template would only recognise this plugin's own output.
     * A message written by someone — or something — else may carry the same ticket as
     * `PROJ-4711:` or `(#4711)`, and stamping it again would duplicate the number.
     *
     * The ticket has to appear as a standalone token, bounded by anything that is not a
     * letter or digit, so `4711` does not count as present in `47110` or `v4711`.
     */
    fun mentionsTicket(message: String, ticket: String): Boolean {
        if (ticket.isEmpty()) return false

        var index = message.indexOf(ticket)
        while (index >= 0) {
            val end = index + ticket.length
            val startsCleanly = index == 0 || !message[index - 1].isLetterOrDigit()
            val endsCleanly = end == message.length || !message[end].isLetterOrDigit()
            if (startsCleanly && endsCleanly) return true
            index = message.indexOf(ticket, index + 1)
        }

        return false
    }
}
