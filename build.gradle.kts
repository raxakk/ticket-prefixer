plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10"
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = "io.github.raxakk"
version = "1.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// The IntelliJ Platform is compiled against Java 21 (class file major version 65),
// regardless of the JBR version the IDE runs on.
kotlin {
    jvmToolchain(21)
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.2.1")
        bundledPlugin("Git4Idea")
        // GitRepository/GitRepositoryManager inherit from the shared DVCS types,
        // which live in these platform modules rather than in Git4Idea itself.
        bundledModule("intellij.platform.vcs.dvcs")
        bundledModule("intellij.platform.vcs.dvcs.impl")

        pluginVerifier()
        zipSigner()
    }

    testImplementation(kotlin("test"))
}

intellijPlatform {
    pluginConfiguration {
        name = "Ticket Prefixer"
        version = project.version.toString()

        vendor {
            name = "raxakk"
            url = "https://phiri.me"
        }

        description = """
            Adds a button to the commit toolbar that writes the ticket number from your
            current Git branch into the commit message — when you press it, not before.
            <br><br>
            That timing is the point. Assistants like Copilot replace the entire contents
            of the commit message field when they generate a message, so a ticket number
            that was inserted automatically when the commit dialog opened is gone by the
            time you commit. Here you let the assistant write first and press the button
            afterwards: nothing touches the field again, and the ticket survives.
            <br><br>
            The ticket is found by a regular expression you control, so any branch scheme
            works — <code>PROJ-4711</code>, bare digits, or a convention of your own.
            Presets for Jira keys and for numeric ids fill that in for you if you would
            rather not write one. A template controls where the number lands:
            <code>#{ticket}: {message}</code> prepends, <code>{message} (#{ticket})</code>
            appends. If the message already opens with the ticket in some other format —
            which is what a generated one tends to do — that reference is rewritten into
            your template instead of a second one being added.
            <br><br>
            The insertion is a normal undoable edit and keeps the caret where it was.
        """.trimIndent()

        changeNotes = """
            Renamed from TicketStamp to Ticket Prefixer. This is a normal update: your
            branch pattern, your message template and any keyboard shortcut you assigned
            to the button all carry over. Only the name changes — in the plugin list, in
            the settings page and on the notifications.
            <br><br>
            New: preset buttons on the settings page for Jira keys and for numeric ids.
            The numeric preset is the shipped default; the Jira one reads
            <code>PROJ-1234</code> whole, where the numeric pattern would have kept only
            the <code>1234</code>. Both just fill the two fields, which stay editable.
            <br><br>
            New: when the commit message already opens with the ticket in a different
            format, which is what an AI-generated message tends to produce, that reference
            is rewritten into your configured template instead of a second one being added
            in front of it. A mention further inside the text is left untouched.
        """.trimIndent()

        ideaVersion {
            // Built against 2026.2, but only stable long-standing APIs are used,
            // so 2026.1 is supported as well.
            sinceBuild = "261"
            untilBuild = provider { null }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
