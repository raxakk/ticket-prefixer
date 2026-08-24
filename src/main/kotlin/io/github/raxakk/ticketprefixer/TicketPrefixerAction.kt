package io.github.raxakk.ticketprefixer

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import git4idea.branch.GitBranchUtil
import git4idea.repo.GitRepositoryManager

/**
 * Writes the ticket number of the current Git branch into the commit message.
 *
 * Registered in `Vcs.MessageActionGroup`, i.e. the small toolbar next to the
 * commit message field in both the Commit tool window and the commit dialog.
 */
class TicketPrefixerAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled =
            e.project != null && e.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val document = e.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT) ?: return
        val settings = TicketPrefixerSettings.getInstance().state

        if (!TicketExtractor.isValidPattern(settings.branchPattern)) {
            notify(
                project,
                "Invalid branch pattern",
                "'${settings.branchPattern}' is not a valid regular expression. " +
                    "Fix it in Settings | Version Control | Ticket Prefixer."
            )
            return
        }

        val branchName = currentBranchName(project, e)
        if (branchName == null) {
            notify(project, "No Git branch found", "Could not determine the current branch.")
            return
        }

        val ticket = TicketExtractor.extract(branchName, settings.branchPattern)
        if (ticket == null) {
            notify(
                project,
                "No ticket number found",
                "The branch '$branchName' does not match the configured pattern."
            )
            return
        }

        val (before, after) = MessageTemplate.render(settings.messageTemplate, ticket)
        val text = document.text

        // An AI-generated message often opens with the ticket in its own format, because
        // it picked the number off the branch name. That leading run gets swapped for the
        // configured rendering; a mention deeper in the text is left alone instead, since
        // moving it to the front would mangle the sentence around it.
        val leadingReference = MessageTemplate.leadingTicketReferenceLength(text, ticket)
        if (leadingReference == 0 && MessageTemplate.mentionsTicket(text, ticket)) return

        // Covers the message already being in exactly the configured shape, which is what
        // makes a second press a no-op.
        if (before + text.substring(leadingReference) + after == text) return

        // Going through the document (rather than CommitMessageI.setCommitMessage) keeps
        // the caret in place and makes the edit undoable. Appending before prepending
        // keeps the offset for the leading part valid.
        WriteCommandAction.runWriteCommandAction(project, "Insert Ticket Number", null, {
            if (leadingReference > 0) document.deleteString(0, leadingReference)
            if (after.isNotEmpty()) document.insertString(document.textLength, after)
            if (before.isNotEmpty()) document.insertString(0, before)
        })
    }

    /**
     * Resolves the repository the commit actually belongs to, which matters in
     * projects with more than one Git root.
     */
    private fun currentBranchName(project: Project, e: AnActionEvent): String? {
        val repository = GitBranchUtil.guessRepositoryForOperation(project, e.dataContext)
            ?: GitRepositoryManager.getInstance(project).repositories.singleOrNull()
        return repository?.currentBranchName
    }

    private fun notify(project: Project, title: String, content: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(title, content, NotificationType.WARNING)
            .notify(project)
    }

    private companion object {
        const val NOTIFICATION_GROUP_ID = "Ticket Prefixer"
    }
}
