# Ticket Prefixer

Prepends the ticket number from your Git branch to the IntelliJ commit message.

## What it does

Ticket Prefixer adds a button to the commit toolbar — the small row of icons next to the
commit message field, in both the Commit tool window and the commit dialog.

Pressing it reads the current Git branch, extracts the ticket number and writes it into
the commit message. Out of the box it prepends:

| Branch | Commit message becomes |
| --- | --- |
| `feature/123456789-branch-name` | `#123456789: your message` |
| `feature/123456789/branch-name` | `#123456789: your message` |
| `bugfix/123456789_branch_name`  | `#123456789: your message` |
| `123456789-branch-name`         | `#123456789: your message` |

Both the pattern used to find the number and the way it is written into the message are
configurable — see below.

## Works with AI-generated commit messages

Copilot and the other assistants that generate a commit message replace the entire
contents of the message field. Anything already in there is gone — including a ticket
number, which is a [long-standing complaint](https://github.com/orgs/community/discussions/185707)
among people who work with issue keys.

That is why this is a button rather than something automatic. Plugins that fill the ticket
in when the commit dialog opens write it before the assistant runs, so the generated
message overwrites it. Here the order is yours to pick:

1. Let the assistant write the message.
2. Press the button.

Because nothing else touches the field afterwards, the ticket survives to the commit.

## Configuration

**Settings → Version Control → Ticket Prefixer**

Both halves of the job are configurable, and two preset buttons fill them in for the
schemes most people are on.

### Presets

| Preset | Branch | Commit message becomes |
| --- | --- | --- |
| **Numeric** *(default)* | `feature/12345678-name` | `#12345678: message` |
| **Jira** | `feature/PROJ-1234-name` | `PROJ-1234: message` |

A preset only fills the two fields below; they stay editable afterwards, so it is a
starting point rather than a mode. **Numeric** doubles as the way back to the shipped
defaults.

Reach for **Jira** if your branches carry project keys. The numeric default does not fail
on them — it matches and quietly throws the key away, turning `feature/PROJ-1234-name`
into `#1234: message` rather than `PROJ-1234: message`. That silent half-right result is
what the preset exists to avoid.

The Jira preset matches upper-case keys only. A case-insensitive version would read
`fix-1234` in `feature/fix-1234-login` as a project key; if your branches really do carry
lower-case keys, put `(?i)` in front of the pattern yourself.

### Branch pattern

The regular expression matched against the branch name. If it declares a capturing
group, the first non-empty group is the ticket number; without a group the whole match
is used.

```
(?:^|[/\-_])(\d{4,})(?=[/\-_]|$)     # default
PROJ-(\d+)                            # JIRA-style keys -> 4711 from PROJ-4711
PROJ-\d+                              # no group -> the whole key, PROJ-4711
```

The default requires the number to form a complete segment of the branch name,
delimited by `/`, `-`, `_` or the start/end of the name, and to be at least four digits
long — which keeps segments like `v2` or `fix-3` from being mistaken for a ticket.

An invalid expression is rejected when you press *Apply*, so it cannot break the button.

### Message template

Controls where the ticket lands. `{ticket}` is the extracted number, `{message}` is the
text already in the field:

| Template | `fix login` becomes |
| --- | --- |
| `#{ticket}: {message}` *(default)* | `#123456789: fix login` |
| `{message} (#{ticket})` | `fix login (#123456789)` |
| `[{ticket}] {message}` | `[123456789] fix login` |
| `#{ticket}:` | `#123456789: fix login` |

A template without `{message}` is treated as a plain prefix, so the last two rows behave
identically.

The insertion is a normal undoable edit, so <kbd>Cmd</kbd>/<kbd>Ctrl</kbd> + <kbd>Z</kbd>
reverts it, and the caret stays where you left it.

The button also recognises a ticket that is already there in a different format, which is
what a generated message usually contains:

| Commit message | Pressing the button |
| --- | --- |
| `fix login` | `#123456789: fix login` |
| `123456789: fix login` | `#123456789: fix login` — the reference is rewritten |
| `[123456789] fix login` | `#123456789: fix login` |
| `#123456789: fix login` | nothing, it is already in shape |
| `fix login, refs 123456789` | nothing — moving the number would mangle the sentence |
| `bump timeout to 1234567890` | `#123456789: bump timeout to 1234567890` |

The last row is the point of the boundary check: `123456789` is only part of `1234567890`,
so it does not count as present. The same goes for `v123456789`.

## Requirements

- IntelliJ IDEA 2026.1 or newer (any IntelliJ-based IDE with the Git plugin enabled)

## Building from source

```bash
./gradlew build          # compile, run tests, assemble the plugin
./gradlew runIde         # launch a sandbox IDE with the plugin installed
./gradlew verifyPlugin   # run the JetBrains Plugin Verifier
```

The distributable ZIP lands in `build/distributions/`.

## License

MIT
