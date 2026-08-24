package io.github.raxakk.ticketprefixer

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class TicketPrefixerConfigurable : Configurable {

    private var branchPatternField: JBTextField? = null
    private var messageTemplateField: JBTextField? = null

    override fun getDisplayName(): String = "Ticket Prefixer"

    override fun createComponent(): JComponent {
        val branchField = JBTextField().also { branchPatternField = it }
        val templateField = JBTextField().also { messageTemplateField = it }

        val restoreDefaults = JButton("Restore Defaults").apply {
            addActionListener {
                branchField.text = TicketExtractor.DEFAULT_BRANCH_PATTERN
                templateField.text = MessageTemplate.DEFAULT
            }
        }

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Branch pattern:", branchField, 1, false)
            .addComponentToRightColumn(
                hint(
                    "Regular expression matched against the branch name. The first capturing " +
                        "group is the ticket number; without a group the whole match is used.<br>" +
                        "Default matches <code>feature/123456789-name</code> and " +
                        "<code>feature/123456789/name</code>."
                )
            )
            .addLabeledComponent("Message template:", templateField, 1, false)
            .addComponentToRightColumn(
                hint(
                    "<code>{ticket}</code> is the extracted number, <code>{message}</code> is the " +
                        "text already in the field.<br>" +
                        "<code>#{ticket}: {message}</code> prepends, " +
                        "<code>{message} (#{ticket})</code> appends. " +
                        "Without <code>{message}</code> the template is used as a plain prefix."
                )
            )
            .addComponent(restoreDefaults)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    private fun hint(html: String) = JBLabel("<html>$html</html>").apply {
        foreground = UIUtil.getContextHelpForeground()
        font = JBUI.Fonts.smallFont()
    }

    override fun isModified(): Boolean {
        val state = TicketPrefixerSettings.getInstance().state
        return branchPatternField?.text != state.branchPattern ||
            messageTemplateField?.text != state.messageTemplate
    }

    override fun apply() {
        val branchPattern = branchPatternField?.text.orEmpty()
        val messageTemplate = messageTemplateField?.text.orEmpty()

        if (branchPattern.isBlank()) {
            throw ConfigurationException("The branch pattern must not be empty.")
        }
        if (!TicketExtractor.isValidPattern(branchPattern)) {
            throw ConfigurationException("'$branchPattern' is not a valid regular expression.")
        }
        if (!messageTemplate.contains(MessageTemplate.TICKET_PLACEHOLDER)) {
            throw ConfigurationException(
                "The message template must contain ${MessageTemplate.TICKET_PLACEHOLDER}."
            )
        }

        val state = TicketPrefixerSettings.getInstance().state
        state.branchPattern = branchPattern
        state.messageTemplate = messageTemplate
    }

    override fun reset() {
        val state = TicketPrefixerSettings.getInstance().state
        branchPatternField?.text = state.branchPattern
        messageTemplateField?.text = state.messageTemplate
    }

    override fun disposeUIResources() {
        branchPatternField = null
        messageTemplateField = null
    }
}
