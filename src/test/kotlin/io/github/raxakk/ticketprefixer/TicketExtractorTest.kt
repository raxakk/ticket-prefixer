package io.github.raxakk.ticketprefixer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TicketExtractorTest {

    private fun extract(branch: String) =
        TicketExtractor.extract(branch, TicketExtractor.DEFAULT_BRANCH_PATTERN)

    @Test
    fun `extracts ticket separated by a dash`() {
        assertEquals("123456789", extract("feature/123456789-branch-name"))
    }

    @Test
    fun `extracts ticket separated by a slash`() {
        assertEquals("123456789", extract("feature/123456789/branch-name"))
    }

    @Test
    fun `extracts ticket separated by an underscore`() {
        assertEquals("123456789", extract("bugfix/123456789_branch_name"))
    }

    @Test
    fun `extracts ticket at the very end of the branch name`() {
        assertEquals("123456789", extract("feature/123456789"))
    }

    @Test
    fun `extracts ticket without a prefix segment`() {
        assertEquals("123456789", extract("123456789-branch-name"))
    }

    @Test
    fun `returns the first ticket when several numbers are present`() {
        assertEquals("123456789", extract("feature/123456789-fixes-987654321"))
    }

    @Test
    fun `ignores digits glued to letters`() {
        assertNull(extract("feature/abc1234-name"))
        assertNull(extract("feature/1234abc-name"))
    }

    @Test
    fun `ignores numbers shorter than four digits`() {
        assertNull(extract("feature/123-branch-name"))
        assertNull(extract("release/v2"))
    }

    @Test
    fun `returns null when there is no number at all`() {
        assertNull(extract("main"))
        assertNull(extract("feature/branch-name"))
    }

    @Test
    fun `honours a custom pattern with a capturing group`() {
        assertEquals(
            "4711",
            TicketExtractor.extract("feature/PROJ-4711-name", """PROJ-(\d+)""")
        )
    }

    @Test
    fun `uses the whole match when the pattern has no capturing group`() {
        assertEquals(
            "PROJ-4711",
            TicketExtractor.extract("feature/PROJ-4711-name", """PROJ-\d+""")
        )
    }

    @Test
    fun `skips empty optional groups`() {
        assertEquals(
            "4711",
            TicketExtractor.extract("feature/PROJ-4711", """(?:(X-)|PROJ-)(\d+)""")
        )
    }

    @Test
    fun `returns null for an invalid pattern instead of throwing`() {
        assertNull(TicketExtractor.extract("feature/123456789-name", "([0-9"))
    }

    @Test
    fun `reports pattern validity`() {
        assertTrue(TicketExtractor.isValidPattern(TicketExtractor.DEFAULT_BRANCH_PATTERN))
        assertFalse(TicketExtractor.isValidPattern("([0-9"))
    }
}
