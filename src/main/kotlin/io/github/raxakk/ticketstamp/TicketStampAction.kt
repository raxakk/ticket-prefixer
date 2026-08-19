package io.github.raxakk.ticketstamp

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
 * Prepends the ticket number of the current Git branch to the commit message.
 *
 * Registered in `Vcs.MessageActionGroup`, i.e. the small toolbar next to the
 * commit message field in both the Commit tool window and the commit dialog.
 */
class TicketStampAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled =
            e.project != null && e.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val document = e.getData(VcsDataKeys.COMMIT_MESSAGE_DOCUMENT) ?: return

        val branchName = currentBranchName(project, e)
        if (branchName == null) {
            notify(project, "No Git branch found", "Could not determine the current branch.")
            return
        }

        val ticket = TicketExtractor.extract(branchName)
        if (ticket == null) {
            notify(
                project,
                "No ticket number found",
                "The branch '$branchName' does not contain a ticket number."
            )
            return
        }

        val prefix = TicketStampSettings.getInstance().state.formatPattern
            .replace(TicketStampSettings.TICKET_PLACEHOLDER, ticket)

        if (document.text.trimStart().startsWith(prefix)) return

        // Going through the document (rather than CommitMessageI.setCommitMessage)
        // keeps the caret in place and makes the insertion undoable.
        WriteCommandAction.runWriteCommandAction(project, "Insert Ticket Number", null, {
            document.insertString(0, "$prefix ")
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
        const val NOTIFICATION_GROUP_ID = "TicketStamp"
    }
}
