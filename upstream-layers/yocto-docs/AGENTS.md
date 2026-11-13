# AGENTS.md — Yocto Project documentation

Guidance for AI agents working in the `yocto-docs` repository.

## Making changes

- No web PR workflow. The project use **patches on mailing lists**
  (docs@lists.yoctoproject.org) tracked via patchwork
  (https://patchwork.yoctoproject.org/project/docs/list/). Don't assume GitHub
  PR conventions apply.
- One Git commit per logical change (e.g., one commit per variable / family of
  variable documentation, or class documentation).
- The standards described in `documentation/standards.md` should be followed as
  much as possible when making changes.
- The formatting conventions in `documentation/conventions.rst` should also be
  followed when making changes. Those are user-facing conventions about how to
  read and interpret the documentation.
- Commit message format:
  - Summary line prefixed with either:
    - the rST file path *without* the "documentation/"
      prefix (e.g. `ref-manual/classes.rst: document the <classname> class`).
    - the section directory if multiple files from that section are touched in
      the commit (e.g. `ref-manual: Remove the documentation of the
      GROUPMEMS_PARAM variable`). Again, no "documentation/" prefix.
    - `docs-wide: ...` if the commit touches files across multiple sections
      (e.g. `docs-wide: replace hardcoded conf paths to structure.rst refs`).
      Again, no "documentation/" prefix.
  - Check `git log --oneline <path>` for the prefix convention already in use.
  - Body explains what and why the documentation change is. If the commit
    represents a feature addition, also how it was tested.
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
  document.rst: document the <classname> class

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
