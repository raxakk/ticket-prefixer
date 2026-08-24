package io.github.raxakk.ticketprefixer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
// The state name and storage file still carry the plugin's original name. They are what
// existing installations read their settings from, so renaming them would silently reset
// everyone's branch pattern and message template back to the defaults.
@State(
    name = "TicketStampSettings",
    storages = [Storage("ticketStamp.xml")]
)
class TicketPrefixerSettings : PersistentStateComponent<TicketPrefixerSettings.State> {

    class State {
        /** Regex applied to the branch name to find the ticket number. */
        var branchPattern: String = Preset.DEFAULT.branchPattern

        /** Template controlling how the ticket is written into the commit message. */
        var messageTemplate: String = Preset.DEFAULT.messageTemplate
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(): TicketPrefixerSettings =
            ApplicationManager.getApplication().getService(TicketPrefixerSettings::class.java)
    }
}
