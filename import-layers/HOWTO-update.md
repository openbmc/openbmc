The directories here are managed with 'git subtree'.  The syntax for this is
non-intuitive, so it is documented below.

The yocto-poky tree should always be based on a real tag, like 'yocto-2.2'.
The other trees should be the latest commit of the branch named after the
corresponding yocto tree.  Ex. yocto-2.2 == "morty", so "origin/morty".

We always require developers to submit changes in these trees upstream first
and only once they have been merged upstream do we backport them.  When
updating a tree, it is possible that some of the backported commits are not
yet in the sub-tree.  For instance, if we are on yocto-2.1 and upgrading to
yocto-2.2, a developer might have backported a commit from what-will-become
yocto-2.3 and we need to preserve this commit.  Thus, you'll need to take
an inventory of these commits to re-cherry-pick at the end of the sub-tree
update process.

```
# Use git-log to keep track of the current commit.
git log -n1 --oneline

# Run 'subtree split --rejoin' so git will re-calculate how the current
# subtree differs from the original external repository.
git subtree split --prefix=import-layers/meta-openembedded --rejoin

# Run 'subtree pull' to update the subtree.
git subtree pull --prefix=import-layers/meta-openembedded --squash \
        ../imports/meta-openembedded origin/morty

# Use git-log to find the 'squash' commit created by 'subtree pull'.
# Keep record of the 2nd commit titled "Squashed ...".
git log -n2 --oneline

# Return to your original tree state.
git checkout <original>

# Cherry-pick the 'subtree pull' 'squash' commit.
git cherry-pick --strategy=subtree <saved>

# re-apply (cherry-pick) unmerged backports and rebase --interactive to
# 'fixup' them into the subtree  cherry-pick.
git cherry-pick <reapply>
...
git rebase --interactive <original>
```
