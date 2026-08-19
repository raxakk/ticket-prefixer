# TicketStamp

Prepends the ticket number from your Git branch to the IntelliJ commit message.

## What it does

TicketStamp adds a button to the commit toolbar — the small row of icons next to the
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

## Configuration

**Settings → Version Control → TicketStamp**

Both halves of the job are configurable, and a **Restore Defaults** button puts them back.

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
reverts it, and the caret stays where you left it. Pressing the button twice does nothing
the second time.

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
