package io.github.raxakk.ticketprefixer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PresetTest {

    private fun extract(preset: Preset, branch: String) =
        TicketExtractor.extract(branch, preset.branchPattern)

    @Test
    fun `every preset declares a usable pattern`() {
        Preset.entries.forEach {
            assertTrue(
                TicketExtractor.isValidPattern(it.branchPattern),
                "${it.label} declares an invalid regular expression"
            )
        }
    }

    @Test
    fun `every preset template survives the settings validation`() {
        Preset.entries.forEach {
            assertTrue(
                it.messageTemplate.contains(MessageTemplate.TICKET_PLACEHOLDER),
                "${it.label} has a template without ${MessageTemplate.TICKET_PLACEHOLDER}"
            )
        }
    }

    @Test
    fun `the numeric preset is what the plugin ships with`() {
        assertEquals(TicketExtractor.DEFAULT_BRANCH_PATTERN, Preset.NUMERIC.branchPattern)
        assertEquals(MessageTemplate.DEFAULT, Preset.NUMERIC.messageTemplate)
        assertEquals(Preset.NUMERIC, Preset.DEFAULT)
    }

    @Test
    fun `fresh settings start on the default preset`() {
        val state = TicketPrefixerSettings.State()

        assertEquals(Preset.DEFAULT.branchPattern, state.branchPattern)
        assertEquals(Preset.DEFAULT.messageTemplate, state.messageTemplate)
    }

    @Test
    fun `numeric preset reads an all-digit id`() {
        assertEquals("12345678", extract(Preset.NUMERIC, "feature/12345678-fix-login"))
        assertEquals("12345678", extract(Preset.NUMERIC, "feature/12345678/fix-login"))
    }

    @Test
    fun `numeric preset drops the project key on a Jira branch`() {
        // Documents the trap the Jira preset exists for: the pattern matches, it just
        // yields a plausible-looking wrong answer rather than nothing.
        assertEquals("1234", extract(Preset.NUMERIC, "feature/PROJ-1234-fix-login"))
    }

    @Test
    fun `jira preset keeps the whole key`() {
        assertEquals("PROJ-1234", extract(Preset.JIRA, "feature/PROJ-1234-fix-login"))
        assertEquals("PROJ-1234", extract(Preset.JIRA, "feature/PROJ-1234/fix-login"))
        assertEquals("ABC-4711", extract(Preset.JIRA, "bugfix/ABC-4711"))
        assertEquals("ABC-4711", extract(Preset.JIRA, "ABC-4711-fix"))
    }

    @Test
    fun `jira preset ignores lower-case words that look like keys`() {
        assertNull(extract(Preset.JIRA, "feature/fix-1234-login"))
        assertNull(extract(Preset.JIRA, "feature/proj-1234-fix"))
    }

    @Test
    fun `jira preset ignores a purely numeric branch`() {
        assertNull(extract(Preset.JIRA, "feature/12345678-fix-login"))
    }

    @Test
    fun `jira preset renders the key without a hash`() {
        assertEquals(
            "PROJ-1234: " to "",
            MessageTemplate.render(Preset.JIRA.messageTemplate, "PROJ-1234")
        )
    }
}
