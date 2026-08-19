plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10"
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = "io.github.raxakk"
version = "1.0.0"

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
        name = "TicketStamp"
        version = project.version.toString()

        vendor {
            name = "raxakk"
            url = "https://phiri.me"
        }

        description = """
            Adds a button to the IntelliJ commit toolbar that reads the ticket number
            from your current Git branch and prepends it to the commit message.
            <br><br>
            Branch names like <code>feature/123456789-branch-name</code> or
            <code>feature/123456789/branch-name</code> become a commit message starting
            with <code>#123456789:</code>.
            <br><br>
            The prefix format is configurable, so <code>#{ticket}:</code> can be changed
            to whatever your team uses.
        """.trimIndent()

        changeNotes = "Initial release."

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
