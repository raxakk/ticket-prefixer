package io.github.raxakk.ticketstamp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(
    name = "TicketStampSettings",
    storages = [Storage("ticketStamp.xml")]
)
class TicketStampSettings : PersistentStateComponent<TicketStampSettings.State> {

    class State {
        var formatPattern: String = DEFAULT_FORMAT_PATTERN
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        const val DEFAULT_FORMAT_PATTERN: String = "#{ticket}:"

        /** Placeholder replaced by the extracted ticket number. */
        const val TICKET_PLACEHOLDER: String = "{ticket}"

        fun getInstance(): TicketStampSettings =
            ApplicationManager.getApplication().getService(TicketStampSettings::class.java)
    }
}
