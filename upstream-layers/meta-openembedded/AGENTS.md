AGENTS.md — Meta OpenEmbedded Layers

Guidance for AI agents working in the `meta-openembedded` repository.

## Making changes

- No web PR workflow. The project use **patches on mailing lists**
  (openembedded-devel@lists.openembedded.org) tracked via patchwork
  (https://patchwork.yoctoproject.org/project/oe/list/). Don't assume GitHub PR
  conventions apply.
- One Git commit per logical change (e.g., one commit per recipe).
- Commit message format:
    - Summary line is prefixed with the recipe name, e.g. `python3-posix-ipc:`.
      If the commit touches multiple recipes, list them comma separated, e.g.
      `recipe1,recipe2:`.
    - Do **not** put the sub-layer tag (e.g. `[meta-oe]`) in the commit summary
      line; it belongs in the email subject only (see below).
    - Check `git log --oneline <path>` for the prefix convention already in use.
    - Body explains what and why the change is. If the commit represents a
      feature addition, also how it was tested.
    - Prefer explaining _why_ a change was made over restating what's already
      visible in the diff, unless the diff is long/hard to follow or the reason
      for the change isn't known — in those cases, summarize what changed
      instead.
    - Be succinct: use as few sentences and lines as possible while still
      covering what/why/how. Cut restatements, filler, and any detail a reviewer
      wouldn't need (e.g. exact test-run counts, step-by-step narration). One
      short paragraph is usually enough; only add a second if a distinct point
      (e.g. testing) needs it.
    - The most relevant information about the change should be presented first.
    - Reference bugs as `[YOCTO #1234]` when applicable.
    - Use `Reported-by`, `Suggested-by`, `Tested-by`, `Reviewed-by`, `Cc` tags
      where relevant.
- Email subject format:
    - The sub-layer tag comes first, directly followed by the `[PATCH]` tag with
      no space between them: `[meta-oe][PATCH 1/3] recipe: summary`.
    - Generate it with
      `git format-patch --subject-prefix="meta-oe][PATCH" <upstream>`, which
      yields `[meta-oe][PATCH x/y]`; add `-v2` etc. for rerolls
      (`[meta-oe][PATCH v2 x/y]`).
    - Each patch carries the tag of the sub-layer it changes (e.g. `meta-oe`,
      `meta-python`, `meta-networking`, `meta-gnome`). Prefer one series per
      sub-layer; if a series spans sub-layers, set the tag per patch.

### AI-generated code

- **Mandatory**: any AI-generated code/commit must add an `AI-Generated:` tag
  line in the commit message, placed _before_ `Signed-off-by` (if present),
  naming the tool used, e.g.:

    ```
    python3-posix-ipc: fix feature probing instead of forcing -lrt

    AI-Generated: Uses Claude Code
    Signed-off-by: Contributor Name <contributor@example.com>
    ```

- Also add a code comment or other in-patch indication that the code is
  AI-generated (recommended, not just the commit tag).
- **Mandatory**: every commit, and every patch file added to a recipe, must
  carry the human contributor's `Signed-off-by`, placed after `AI-Generated:`.
  Use the contributor's identity from `git config user.name`/`user.email` (e.g.
  `git commit -s`, or `git rebase --signoff <upstream>` for existing commits),
  never one made up for the AI tool. The sign-off certifies the DCO/Developer's
  Certificate of Origin on the contributor's behalf, so they must review the
  change before it is sent.
- In patch files added to a recipe, the header order is `Upstream-Status:`,
  `AI-Generated:`, then `Signed-off-by:`, and the `From:` line uses the same
  identity as the sign-off.
- Contributor is responsible for confirming the AI tool's terms don't conflict
  with the project's open source license/IP policy, and that any third-party
  copyrighted material in the AI output is properly licensed before contributing
  it. See the Linux Foundation generative AI guidance:
  https://www.linuxfoundation.org/legal/generative-ai.
- `Co-authored-by` should be omitted in favor of the `AI-generated` tag.

When in doubt about a convention, grep `git log --oneline <path>` in the target
repo for precedent before inventing a new style.
