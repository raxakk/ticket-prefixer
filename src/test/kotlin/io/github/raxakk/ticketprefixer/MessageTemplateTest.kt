package io.github.raxakk.ticketprefixer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MessageTemplateTest {

    @Test
    fun `default template prepends the ticket`() {
        assertEquals(
            "#4711: " to "",
            MessageTemplate.render(MessageTemplate.DEFAULT, "4711")
        )
    }

    @Test
    fun `template can append the ticket`() {
        assertEquals(
            "" to " (#4711)",
            MessageTemplate.render("{message} (#{ticket})", "4711")
        )
    }

    @Test
    fun `template can wrap the message on both sides`() {
        assertEquals(
            "[4711] " to " [end]",
            MessageTemplate.render("[{ticket}] {message} [end]", "4711")
        )
    }

    @Test
    fun `template without the message placeholder acts as a plain prefix`() {
        assertEquals(
            "#4711: " to "",
            MessageTemplate.render("#{ticket}:", "4711")
        )
    }

    @Test
    fun `ticket may appear more than once`() {
        assertEquals(
            "4711 " to " end-4711",
            MessageTemplate.render("{ticket} {message} end-{ticket}", "4711")
        )
    }

    @Test
    fun `recognises the ticket in this plugin's own rendering`() {
        assertTrue(MessageTemplate.mentionsTicket("#4711: fix login", "4711"))
        assertTrue(MessageTemplate.mentionsTicket("fix login (#4711)", "4711"))
        assertTrue(MessageTemplate.mentionsTicket("[4711] fix login", "4711"))
    }

    @Test
    fun `recognises the ticket in a foreign format`() {
        assertTrue(MessageTemplate.mentionsTicket("4711: fix login", "4711"))
        assertTrue(MessageTemplate.mentionsTicket("fix login, refs 4711", "4711"))
        assertTrue(MessageTemplate.mentionsTicket("PROJ-4711 fix login", "4711"))
    }

    @Test
    fun `recognises a non-numeric ticket`() {
        assertTrue(MessageTemplate.mentionsTicket("PROJ-4711: fix login", "PROJ-4711"))
        assertFalse(MessageTemplate.mentionsTicket("PROJ-4712: fix login", "PROJ-4711"))
    }

    @Test
    fun `does not mistake a longer number for the ticket`() {
        assertFalse(MessageTemplate.mentionsTicket("bump timeout to 47110", "4711"))
        assertFalse(MessageTemplate.mentionsTicket("bump timeout to 04711", "4711"))
    }

    @Test
    fun `does not mistake digits glued to letters for the ticket`() {
        assertFalse(MessageTemplate.mentionsTicket("release v4711 is out", "4711"))
        assertFalse(MessageTemplate.mentionsTicket("fix 4711a handling", "4711"))
    }

    @Test
    fun `reports an absent ticket`() {
        assertFalse(MessageTemplate.mentionsTicket("fix login", "4711"))
        assertFalse(MessageTemplate.mentionsTicket("", "4711"))
    }

    @Test
    fun `finds the ticket even when an earlier occurrence is embedded`() {
        assertTrue(MessageTemplate.mentionsTicket("v4711 breaks 4711", "4711"))
    }

    private fun leading(message: String, ticket: String = "4711") =
        message.substring(0, MessageTemplate.leadingTicketReferenceLength(message, ticket))

    @Test
    fun `recognises a bare leading reference`() {
        assertEquals("4711: ", leading("4711: fix login"))
        assertEquals("4711 ", leading("4711 fix login"))
        assertEquals("4711 - ", leading("4711 - fix login"))
    }

    @Test
    fun `recognises a decorated leading reference`() {
        assertEquals("#4711: ", leading("#4711: fix login"))
        assertEquals("[4711] ", leading("[4711] fix login"))
        assertEquals("(4711) ", leading("(4711) fix login"))
    }

    @Test
    fun `keeps a second bracketed tag out of the leading reference`() {
        assertEquals("[4711] ", leading("[4711] [WIP] fix login"))
    }

    @Test
    fun `covers a message consisting only of the reference`() {
        assertEquals("4711:", leading("4711:"))
        assertEquals("4711", leading("4711"))
    }

    @Test
    fun `ignores a mention that is not at the start`() {
        assertEquals(0, MessageTemplate.leadingTicketReferenceLength("fix login, refs 4711", "4711"))
        assertEquals(0, MessageTemplate.leadingTicketReferenceLength("fix 4711 login", "4711"))
    }

    @Test
    fun `does not read the ticket out of a longer leading number`() {
        assertEquals(0, MessageTemplate.leadingTicketReferenceLength("47110: fix login", "4711"))
        assertEquals(0, MessageTemplate.leadingTicketReferenceLength("4711a fix", "4711"))
    }

    @Test
    fun `handles a non-numeric leading reference`() {
        assertEquals("PROJ-4711: ", leading("PROJ-4711: fix login", "PROJ-4711"))
        assertEquals(0, MessageTemplate.leadingTicketReferenceLength("PROJ-4712: fix", "PROJ-4711"))
    }

    @Test
    fun `reports no reference for an empty message`() {
        assertEquals(0, MessageTemplate.leadingTicketReferenceLength("", "4711"))
    }
}
