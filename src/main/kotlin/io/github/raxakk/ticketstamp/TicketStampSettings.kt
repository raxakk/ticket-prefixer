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
        /** Regex applied to the branch name to find the ticket number. */
        var branchPattern: String = TicketExtractor.DEFAULT_BRANCH_PATTERN

        /** Template controlling how the ticket is written into the commit message. */
        var messageTemplate: String = MessageTemplate.DEFAULT
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(): TicketStampSettings =
            ApplicationManager.getApplication().getService(TicketStampSettings::class.java)
    }
}
