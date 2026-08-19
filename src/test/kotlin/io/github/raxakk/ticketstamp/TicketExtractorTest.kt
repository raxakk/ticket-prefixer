package io.github.raxakk.ticketstamp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TicketExtractorTest {

    @Test
    fun `extracts ticket separated by a dash`() {
        assertEquals("123456789", TicketExtractor.extract("feature/123456789-branch-name"))
    }

    @Test
    fun `extracts ticket separated by a slash`() {
        assertEquals("123456789", TicketExtractor.extract("feature/123456789/branch-name"))
    }

    @Test
    fun `extracts ticket separated by an underscore`() {
        assertEquals("123456789", TicketExtractor.extract("bugfix/123456789_branch_name"))
    }

    @Test
    fun `extracts ticket at the very end of the branch name`() {
        assertEquals("123456789", TicketExtractor.extract("feature/123456789"))
    }

    @Test
    fun `extracts ticket without a prefix segment`() {
        assertEquals("123456789", TicketExtractor.extract("123456789-branch-name"))
    }

    @Test
    fun `returns the first ticket when several numbers are present`() {
        assertEquals("123456789", TicketExtractor.extract("feature/123456789-fixes-987654321"))
    }

    @Test
    fun `ignores digits glued to letters`() {
        assertNull(TicketExtractor.extract("feature/abc1234-name"))
        assertNull(TicketExtractor.extract("feature/1234abc-name"))
    }

    @Test
    fun `ignores numbers shorter than four digits`() {
        assertNull(TicketExtractor.extract("feature/123-branch-name"))
        assertNull(TicketExtractor.extract("release/v2"))
    }

    @Test
    fun `returns null when there is no number at all`() {
        assertNull(TicketExtractor.extract("main"))
        assertNull(TicketExtractor.extract("feature/branch-name"))
    }
}
