# Copyright (C) 2009 Chris Larson <clarson@kergoth.com>
# Released under the MIT license (see COPYING.MIT for the terms)
#
# gitver.bbclass provides a GITVER variable which is a (fairly) sane version,
# for use in ${PV}, extracted from the ${S} git checkout, assuming it is one.
# This is most useful in concert with srctree.bbclass.

def git_drop_tag_prefix(version):
    import re
    if re.match("v\d", version):
        return version[1:]
    else:
        return version

GIT_TAGADJUST = "git_drop_tag_prefix(version)"
GITVER = "${@get_git_pv(d, tagadjust=lambda version:${GIT_TAGADJUST})}"
GITSHA = "${@get_git_hash(d)}"

def gitrev_run(cmd, path):
    (output, error) = bb.process.run(cmd, cwd=path)
    return output.rstrip()

def get_git_pv(d, tagadjust=None):
    import os

    srcdir = d.getVar("EXTERNALSRC") or d.getVar("S")
    gitdir = os.path.abspath(os.path.join(srcdir, ".git"))
    try:
        ver = gitrev_run("git describe --tags", gitdir)
    except:
        try:
            ver = gitrev_run("git rev-parse --short HEAD", gitdir)
            if ver:
                return "0.0+%s" % ver
            else:
                return "0.0"

        except Exception as exc:
            raise bb.parse.SkipRecipe(str(exc))

    if ver and tagadjust:
        ver = tagadjust(ver)
    return ver

def get_git_hash(d):
    import os

    srcdir = d.getVar("EXTERNALSRC") or d.getVar("S")
    gitdir = os.path.abspath(os.path.join(srcdir, ".git"))
    try:
        rev = gitrev_run("git rev-list HEAD -1")
        return rev[:7]
    except Exception as exc:
        bb.fatal(str(exc))

def mark_recipe_dependencies(path, d):
    from bb.parse import mark_dependency

    gitdir = os.path.join(path, ".git")

    # Force the recipe to be reparsed so the version gets bumped
    # if the active branch is switched, or if the branch changes.
    mark_dependency(d, os.path.join(gitdir, "HEAD"))

    # Force a reparse if anything in the index changes.
    mark_dependency(d, os.path.join(gitdir, "index"))

    try:
        ref = gitrev_run("git symbolic-ref -q HEAD", gitdir)
    except bb.process.CmdError:
        pass
    else:
        if ref:
            mark_dependency(d, os.path.join(gitdir, ref))

    # Catch new tags.
    tagdir = os.path.join(gitdir, "refs", "tags")
    if os.path.exists(tagdir):
        mark_dependency(d, tagdir)

python () {
    srcdir = d.getVar("EXTERNALSRC") or d.getVar("S")
    mark_recipe_dependencies(srcdir, d)
}
