package io.github.raxakk.ticketstamp

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import javax.swing.JComponent
import javax.swing.JPanel

class TicketStampConfigurable : Configurable {

    private var formatPatternField: JBTextField? = null

    override fun getDisplayName(): String = "TicketStamp"

    override fun createComponent(): JComponent {
        val field = JBTextField()
        formatPatternField = field

        val hint = JBLabel(
            "<html><code>{ticket}</code> is replaced by the number found in the branch name.<br>" +
                "Example: <code>#{ticket}:</code> produces <code>#123456789: your message</code></html>"
        )
        hint.foreground = UIUtil.getContextHelpForeground()
        hint.font = JBUI.Fonts.smallFont()

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Prefix format:", field, 1, false)
            .addComponentToRightColumn(hint)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean =
        formatPatternField?.text != TicketStampSettings.getInstance().state.formatPattern

    override fun apply() {
        TicketStampSettings.getInstance().state.formatPattern =
            formatPatternField?.text?.takeIf { it.isNotBlank() }
                ?: TicketStampSettings.DEFAULT_FORMAT_PATTERN
    }

    override fun reset() {
        formatPatternField?.text = TicketStampSettings.getInstance().state.formatPattern
    }

    override fun disposeUIResources() {
        formatPatternField = null
    }
}
