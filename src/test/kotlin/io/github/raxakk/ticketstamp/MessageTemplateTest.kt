package io.github.raxakk.ticketstamp

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
