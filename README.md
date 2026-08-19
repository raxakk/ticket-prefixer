# TicketStamp

Prepends the ticket number from your Git branch to the IntelliJ commit message.

## What it does

TicketStamp adds a button to the commit toolbar — the small row of icons next to the
commit message field, in both the Commit tool window and the commit dialog.

Pressing it reads the current Git branch, extracts the ticket number and prepends it
to whatever you have already typed:

| Branch | Commit message becomes |
| --- | --- |
| `feature/123456789-branch-name` | `#123456789: your message` |
| `feature/123456789/branch-name` | `#123456789: your message` |
| `bugfix/123456789_branch_name`  | `#123456789: your message` |
| `123456789-branch-name`         | `#123456789: your message` |

The insertion is a normal undoable edit, so <kbd>Cmd</kbd>/<kbd>Ctrl</kbd> + <kbd>Z</kbd>
reverts it. Pressing the button twice does nothing the second time — the prefix is only
added if it is not already there.

## Configuration

**Settings → Version Control → TicketStamp**

The prefix format defaults to `#{ticket}:`, where `{ticket}` is replaced by the number
found in the branch. Change it to whatever your team uses, for example `{ticket} -` or
`[{ticket}]`.

## How the ticket number is detected

The number has to form a complete segment of the branch name, delimited by `/`, `-`, `_`
or the start/end of the name, and must be at least four digits long. That minimum keeps
segments like `v2` or `fix-3` from being mistaken for a ticket number.

Names without a standalone number — `main`, `feature/branch-name`, `feature/abc1234-name` —
produce a notification instead of a change to the message.

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
