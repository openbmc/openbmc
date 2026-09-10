# AGENTS.md — OpenEmbedded-Core & BitBake

Guidance for AI agents working in the `openembedded-core` repository.

## Making changes

- No web PR workflow. The project use **patches on mailing lists**
  (openembedded-core@lists.openembedded.org) tracked via patchwork. Don't
  assume GitHub PR conventions apply.
- One Git commit per logical change (e.g., one commit per recipe added/upgraded).
- Commit message format:
  - Summary line prefixed with the recipe name or short file path,
    e.g. `openssl: upgrade 3.2.0 -> 3.2.1` or `classes/rootfs-postcommands: fix typo`.
    Check `git log --oneline <path>` for the prefix convention already in use.
  - Body explains what/why/how, and how it was tested.
  - Prefer explaining *why* a change was made over restating what's
    already visible in the diff, unless the diff is long/hard to follow
    or the reason for the change isn't known — in those cases, summarize
    what changed instead.
  - Be succinct: use as few sentences and lines as possible while still
    covering what/why/how. Cut restatements, filler, and any detail a
    reviewer wouldn't need (e.g. exact test-run counts, step-by-step
    narration). One short paragraph is usually enough; only add a second
    if a distinct point (e.g. testing) needs it.
  - The most relevant information about the change should be presented first.
  - Reference bugs as `[YOCTO #1234]` when applicable.
  - Use `Reported-by`, `Suggested-by`, `Tested-by`, `Reviewed-by`, `Cc` tags
    where relevant.

### AI-generated code

- **Mandatory**: any AI-generated code/commit must add an `AI-Generated:`
  tag line in the commit message, placed *before* `Signed-off-by` (if present),
  naming the tool used, e.g.:

  ```
  component: Add the ability to ...

  AI-Generated: Uses GitHub Copilot
  ```

- Also add a code comment or other in-patch indication that the code is
  AI-generated (recommended, not just the commit tag).
- **Mandatory** Do not automatically add a `Signed-off-by`. This tag must
  always be written by the human contributor, never by the AI tool — the
  DCO/Developer's Statement of Origin still applies in full.
- Contributor is responsible for confirming the AI tool's terms don't
  conflict with the project's open source license/IP policy, and that
  any third-party copyrighted material in the AI output is properly
  licensed before contributing it. See the Linux Foundation generative AI
  guidance: https://www.linuxfoundation.org/legal/generative-ai.
- `Co-authored-by` should be omitted in favor of the `AI-generated` tag.

When in doubt about a convention, grep `git log --oneline <path>` in the
target repo for precedent before inventing a new style.
