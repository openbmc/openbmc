# gitpkgv.bbclass provides a GITPKGV and GITPKGVTAG variables to be
# used in PKGV, as described bellow:
#
# - GITPKGV which is a sortable version with the format NN+GITHASH, to
#   be used in PKGV, where
#
#   NN equals the total number of revs up to SRCREV
#   GITHASH is SRCREV's (full) hash
#
# - GITPKGVTAG which is the output of 'git describe --tags --exact-match'
#   allowing for automatic versioning
#
# gitpkgv.bbclass assumes the git repository has been cloned, and
# contains SRCREV. So ${GITPKGV} and ${GITPKGVTAG} should never be
# used in PV, only in PKGV.  It can handle SRCREV = ${AUTOREV}, as
# well as SRCREV = "<some fixed git hash>".
#
# WARNING: if upstream repository is always using consistent and
# sortable tag name scheme you can get sortable version including tag
# name with ${GITPKGVTAG}, but be aware that ie tag sequence "v1.0,
# v1.2, xtest, v2.0" will force you to increment PE to get upgradeable
# path to v2.0 revisions
#
# use example:
#
# inherit gitpkgv
#
# PV = "1.0+git"               # expands to 1.0+git
# PKGV = "1.0+git${GITPKGV}"   # expands also to something like 1.0+git31337+4c1c21d7d
#
# or
#
# inherit gitpkgv
#
# PV = "1.0+git"               # expands to 1.0+git
# PKGV = "${GITPKGVTAG}"       # expands to something like 1.0-31337+g4c1c21d
#                                if there is tag v1.0 before this revision or
#                                ver1.0-31337+g4c1c21d if there is tag ver1.0

GITPKGV = "${@get_git_pkgv(d, False)}"
GITPKGVTAG = "${@get_git_pkgv(d, True)}"

# This regexp is used to drop unwanted parts of the found tags. Any matching
# groups will be concatenated to yield the final version.
GITPKGV_TAG_REGEXP ??= "v(\d.*)"

def gitpkgv_drop_tag_prefix(d, version):
    import re

    m = re.match(d.getVar('GITPKGV_TAG_REGEXP'), version)
    if m:
        return ''.join(group for group in m.groups() if group)
    else:
        return version

def get_git_pkgv(d, use_tags):
    import os
    import bb
    from shlex import quote

    src_uri = d.getVar('SRC_URI').split()
    fetcher = bb.fetch2.Fetch(src_uri, d)
    ud = fetcher.ud

    #
    # If SRCREV_FORMAT is set respect it for tags
    #
    format = d.getVar('SRCREV_FORMAT')
    if not format:
        names = []
        for url in ud.values():
            if url.type == 'git' or url.type == 'gitsm':
                names.extend(url.revisions.keys())
        if len(names) > 0:
            format = '_'.join(names)
        else:
            format = 'default'

    found = False
    for url in ud.values():
        if url.type == 'git' or url.type == 'gitsm':
            for name, rev in url.revisions.items():
                if not os.path.exists(url.localpath):
                    return None

                found = True

                vars = { 'repodir' : quote(url.localpath),
                         'rev' : quote(rev) }

                rev = bb.fetch2.get_srcrev(d).split('+')[1]
                rev_file = os.path.join(url.localpath, "oe-gitpkgv_" + rev)

                if not os.path.exists(rev_file) or os.path.getsize(rev_file)==0:
                    commits = bb.fetch2.runfetchcmd(
                        "git --git-dir=%(repodir)s rev-list %(rev)s -- 2>/dev/null | wc -l"
                        % vars, d, quiet=True).strip().lstrip('0')

                    if commits != "":
                        oe.path.remove(rev_file, recurse=False)
                        with open(rev_file, "w") as f:
                            f.write("%d\n" % int(commits))
                    else:
                        commits = "0"
                else:
                    with open(rev_file, "r") as f:
                        commits = f.readline(128).strip()

                if use_tags:
                    try:
                        output = bb.fetch2.runfetchcmd(
                            "git --git-dir=%(repodir)s describe %(rev)s --tags --exact-match 2>/dev/null"
                            % vars, d, quiet=True).strip()
                        ver = gitpkgv_drop_tag_prefix(d, output)
                    except Exception:
                        ver = "0.0-%s-g%s" % (commits, vars['rev'][:7])
                else:
                    ver = "%s+%s" % (commits, vars['rev'][:7])

                format = format.replace(name, ver)

    if found:
        return format

    return '0+0'
