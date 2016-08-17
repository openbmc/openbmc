# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' git implementation

git fetcher support the SRC_URI with format of:
SRC_URI = "git://some.host/somepath;OptionA=xxx;OptionB=xxx;..."

Supported SRC_URI options are:

- branch
   The git branch to retrieve from. The default is "master"

   This option also supports multiple branch fetching, with branches
   separated by commas.  In multiple branches case, the name option
   must have the same number of names to match the branches, which is
   used to specify the SRC_REV for the branch
   e.g:
   SRC_URI="git://some.host/somepath;branch=branchX,branchY;name=nameX,nameY"
   SRCREV_nameX = "xxxxxxxxxxxxxxxxxxxx"
   SRCREV_nameY = "YYYYYYYYYYYYYYYYYYYY"

- tag
    The git tag to retrieve. The default is "master"

- protocol
   The method to use to access the repository. Common options are "git",
   "http", "https", "file", "ssh" and "rsync". The default is "git".

- rebaseable
   rebaseable indicates that the upstream git repo may rebase in the future,
   and current revision may disappear from upstream repo. This option will
   remind fetcher to preserve local cache carefully for future use.
   The default value is "0", set rebaseable=1 for rebaseable git repo.

- nocheckout
   Don't checkout source code when unpacking. set this option for the recipe
   who has its own routine to checkout code.
   The default is "0", set nocheckout=1 if needed.

- bareclone
   Create a bare clone of the source code and don't checkout the source code
   when unpacking. Set this option for the recipe who has its own routine to
   checkout code and tracking branch requirements.
   The default is "0", set bareclone=1 if needed.

- nobranch
   Don't check the SHA validation for branch. set this option for the recipe
   referring to commit which is valid in tag instead of branch.
   The default is "0", set nobranch=1 if needed.

"""

#Copyright (C) 2005 Richard Purdie
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import errno
import os
import re
import bb
import errno
from   bb    import data
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger

class Git(FetchMethod):
    """Class to fetch a module or modules from git repositories"""
    def init(self, d):
        pass

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with git.
        """
        return ud.type in ['git']

    def supports_checksum(self, urldata):
        return False

    def urldata_init(self, ud, d):
        """
        init git specific variable within url data
        so that the git method like latest_revision() can work
        """
        if 'protocol' in ud.parm:
            ud.proto = ud.parm['protocol']
        elif not ud.host:
            ud.proto = 'file'
        else:
            ud.proto = "git"

        if not ud.proto in ('git', 'file', 'ssh', 'http', 'https', 'rsync'):
            raise bb.fetch2.ParameterError("Invalid protocol type", ud.url)

        ud.nocheckout = ud.parm.get("nocheckout","0") == "1"

        ud.rebaseable = ud.parm.get("rebaseable","0") == "1"

        ud.nobranch = ud.parm.get("nobranch","0") == "1"

        # bareclone implies nocheckout
        ud.bareclone = ud.parm.get("bareclone","0") == "1"
        if ud.bareclone:
            ud.nocheckout = 1
  
        ud.unresolvedrev = {}
        branches = ud.parm.get("branch", "master").split(',')
        if len(branches) != len(ud.names):
            raise bb.fetch2.ParameterError("The number of name and branch parameters is not balanced", ud.url)
        ud.branches = {}
        for name in ud.names:
            branch = branches[ud.names.index(name)]
            ud.branches[name] = branch
            ud.unresolvedrev[name] = branch

        ud.basecmd = data.getVar("FETCHCMD_git", d, True) or "git -c core.fsyncobjectfiles=0"

        ud.write_tarballs = ((data.getVar("BB_GENERATE_MIRROR_TARBALLS", d, True) or "0") != "0") or ud.rebaseable

        ud.setup_revisons(d)

        for name in ud.names:
            # Ensure anything that doesn't look like a sha256 checksum/revision is translated into one
            if not ud.revisions[name] or len(ud.revisions[name]) != 40  or (False in [c in "abcdef0123456789" for c in ud.revisions[name]]):
                if ud.revisions[name]:
                    ud.unresolvedrev[name] = ud.revisions[name]
                ud.revisions[name] = self.latest_revision(ud, d, name)

        gitsrcname = '%s%s' % (ud.host.replace(':', '.'), ud.path.replace('/', '.').replace('*', '.'))
        if gitsrcname.startswith('.'):
            gitsrcname = gitsrcname[1:]

        # for rebaseable git repo, it is necessary to keep mirror tar ball
        # per revision, so that even the revision disappears from the
        # upstream repo in the future, the mirror will remain intact and still
        # contains the revision
        if ud.rebaseable:
            for name in ud.names:
                gitsrcname = gitsrcname + '_' + ud.revisions[name]
        ud.mirrortarball = 'git2_%s.tar.gz' % (gitsrcname)
        ud.fullmirror = os.path.join(d.getVar("DL_DIR", True), ud.mirrortarball)
        gitdir = d.getVar("GITDIR", True) or (d.getVar("DL_DIR", True) + "/git2/")
        ud.clonedir = os.path.join(gitdir, gitsrcname)

        ud.localfile = ud.clonedir

    def localpath(self, ud, d):
        return ud.clonedir

    def need_update(self, ud, d):
        if not os.path.exists(ud.clonedir):
            return True
        os.chdir(ud.clonedir)
        for name in ud.names:
            if not self._contains_ref(ud, d, name):
                return True
        if ud.write_tarballs and not os.path.exists(ud.fullmirror):
            return True
        return False

    def try_premirror(self, ud, d):
        # If we don't do this, updating an existing checkout with only premirrors
        # is not possible
        if d.getVar("BB_FETCH_PREMIRRORONLY", True) is not None:
            return True
        if os.path.exists(ud.clonedir):
            return False
        return True

    def download(self, ud, d):
        """Fetch url"""

        # If the checkout doesn't exist and the mirror tarball does, extract it
        if not os.path.exists(ud.clonedir) and os.path.exists(ud.fullmirror):
            bb.utils.mkdirhier(ud.clonedir)
            os.chdir(ud.clonedir)
            runfetchcmd("tar -xzf %s" % (ud.fullmirror), d)

        repourl = self._get_repo_url(ud)

        # If the repo still doesn't exist, fallback to cloning it
        if not os.path.exists(ud.clonedir):
            # We do this since git will use a "-l" option automatically for local urls where possible
            if repourl.startswith("file://"):
                repourl = repourl[7:]
            clone_cmd = "%s clone --bare --mirror %s %s" % (ud.basecmd, repourl, ud.clonedir)
            if ud.proto.lower() != 'file':
                bb.fetch2.check_network_access(d, clone_cmd)
            runfetchcmd(clone_cmd, d)

        os.chdir(ud.clonedir)
        # Update the checkout if needed
        needupdate = False
        for name in ud.names:
            if not self._contains_ref(ud, d, name):
                needupdate = True
        if needupdate:
            try: 
                runfetchcmd("%s remote rm origin" % ud.basecmd, d) 
            except bb.fetch2.FetchError:
                logger.debug(1, "No Origin")

            runfetchcmd("%s remote add --mirror=fetch origin %s" % (ud.basecmd, repourl), d)
            fetch_cmd = "%s fetch -f --prune %s refs/*:refs/*" % (ud.basecmd, repourl)
            if ud.proto.lower() != 'file':
                bb.fetch2.check_network_access(d, fetch_cmd, ud.url)
            runfetchcmd(fetch_cmd, d)
            runfetchcmd("%s prune-packed" % ud.basecmd, d)
            runfetchcmd("%s pack-redundant --all | xargs -r rm" % ud.basecmd, d)
            try:
                os.unlink(ud.fullmirror)
            except OSError as exc:
                if exc.errno != errno.ENOENT:
                    raise
        os.chdir(ud.clonedir)
        for name in ud.names:
            if not self._contains_ref(ud, d, name):
                raise bb.fetch2.FetchError("Unable to find revision %s in branch %s even from upstream" % (ud.revisions[name], ud.branches[name]))

    def build_mirror_data(self, ud, d):
        # Generate a mirror tarball if needed
        if ud.write_tarballs and not os.path.exists(ud.fullmirror):
            # it's possible that this symlink points to read-only filesystem with PREMIRROR
            if os.path.islink(ud.fullmirror):
                os.unlink(ud.fullmirror)

            os.chdir(ud.clonedir)
            logger.info("Creating tarball of git repository")
            runfetchcmd("tar -czf %s %s" % (ud.fullmirror, os.path.join(".") ), d)
            runfetchcmd("touch %s.done" % (ud.fullmirror), d)

    def unpack(self, ud, destdir, d):
        """ unpack the downloaded src to destdir"""

        subdir = ud.parm.get("subpath", "")
        if subdir != "":
            readpathspec = ":%s" % (subdir)
            def_destsuffix = "%s/" % os.path.basename(subdir.rstrip('/'))
        else:
            readpathspec = ""
            def_destsuffix = "git/"

        destsuffix = ud.parm.get("destsuffix", def_destsuffix)
        destdir = ud.destdir = os.path.join(destdir, destsuffix)
        if os.path.exists(destdir):
            bb.utils.prunedir(destdir)

        cloneflags = "-s -n"
        if ud.bareclone:
            cloneflags += " --mirror"

        runfetchcmd("%s clone %s %s/ %s" % (ud.basecmd, cloneflags, ud.clonedir, destdir), d)
        os.chdir(destdir)
        repourl = self._get_repo_url(ud)
        runfetchcmd("%s remote set-url origin %s" % (ud.basecmd, repourl), d)
        if not ud.nocheckout:
            if subdir != "":
                runfetchcmd("%s read-tree %s%s" % (ud.basecmd, ud.revisions[ud.names[0]], readpathspec), d)
                runfetchcmd("%s checkout-index -q -f -a" % ud.basecmd, d)
            elif not ud.nobranch:
                branchname =  ud.branches[ud.names[0]]
                runfetchcmd("%s checkout -B %s %s" % (ud.basecmd, branchname, \
                            ud.revisions[ud.names[0]]), d)
                runfetchcmd("%s branch --set-upstream %s origin/%s" % (ud.basecmd, branchname, \
                            branchname), d)
            else:
                runfetchcmd("%s checkout %s" % (ud.basecmd, ud.revisions[ud.names[0]]), d)

        return True

    def clean(self, ud, d):
        """ clean the git directory """

        bb.utils.remove(ud.localpath, True)
        bb.utils.remove(ud.fullmirror)
        bb.utils.remove(ud.fullmirror + ".done")

    def supports_srcrev(self):
        return True

    def _contains_ref(self, ud, d, name):
        cmd = ""
        if ud.nobranch:
            cmd = "%s log --pretty=oneline -n 1 %s -- 2> /dev/null | wc -l" % (
                ud.basecmd, ud.revisions[name])
        else:
            cmd =  "%s branch --contains %s --list %s 2> /dev/null | wc -l" % (
                ud.basecmd, ud.revisions[name], ud.branches[name])
        try:
            output = runfetchcmd(cmd, d, quiet=True)
        except bb.fetch2.FetchError:
            return False
        if len(output.split()) > 1:
            raise bb.fetch2.FetchError("The command '%s' gave output with more then 1 line unexpectedly, output: '%s'" % (cmd, output))
        return output.split()[0] != "0"

    def _get_repo_url(self, ud):
        """
        Return the repository URL
        """
        if ud.user:
            username = ud.user + '@'
        else:
            username = ""
        return "%s://%s%s%s" % (ud.proto, username, ud.host, ud.path)

    def _revision_key(self, ud, d, name):
        """
        Return a unique key for the url
        """
        return "git:" + ud.host + ud.path.replace('/', '.') + ud.unresolvedrev[name]

    def _lsremote(self, ud, d, search):
        """
        Run git ls-remote with the specified search string
        """
        repourl = self._get_repo_url(ud)
        cmd = "%s ls-remote %s %s" % \
              (ud.basecmd, repourl, search)
        if ud.proto.lower() != 'file':
            bb.fetch2.check_network_access(d, cmd)
        output = runfetchcmd(cmd, d, True)
        if not output:
            raise bb.fetch2.FetchError("The command %s gave empty output unexpectedly" % cmd, ud.url)
        return output

    def _latest_revision(self, ud, d, name):
        """
        Compute the HEAD revision for the url
        """
        output = self._lsremote(ud, d, "")
        # Tags of the form ^{} may not work, need to fallback to other form
        if ud.unresolvedrev[name][:5] == "refs/":
            head = ud.unresolvedrev[name]
            tag = ud.unresolvedrev[name]
        else:
            head = "refs/heads/%s" % ud.unresolvedrev[name]
            tag = "refs/tags/%s" % ud.unresolvedrev[name]
        for s in [head, tag + "^{}", tag]:
            for l in output.split('\n'):
                if s in l:
                    return l.split()[0]
        raise bb.fetch2.FetchError("Unable to resolve '%s' in upstream git repository in git ls-remote output for %s" % \
            (ud.unresolvedrev[name], ud.host+ud.path))

    def latest_versionstring(self, ud, d):
        """
        Compute the latest release name like "x.y.x" in "x.y.x+gitHASH"
        by searching through the tags output of ls-remote, comparing
        versions and returning the highest match.
        """
        pupver = ('', '')

        tagregex = re.compile(d.getVar('UPSTREAM_CHECK_GITTAGREGEX', True) or "(?P<pver>([0-9][\.|_]?)+)")
        try:
            output = self._lsremote(ud, d, "refs/tags/*")
        except bb.fetch2.FetchError or bb.fetch2.NetworkAccess:
            return pupver

        verstring = ""
        revision = ""
        for line in output.split("\n"):
            if not line:
                break

            tag_head = line.split("/")[-1]
            # Ignore non-released branches
            m = re.search("(alpha|beta|rc|final)+", tag_head)
            if m:
                continue

            # search for version in the line
            tag = tagregex.search(tag_head)
            if tag == None:
                continue

            tag = tag.group('pver')
            tag = tag.replace("_", ".")

            if verstring and bb.utils.vercmp(("0", tag, ""), ("0", verstring, "")) < 0:
                continue

            verstring = tag
            revision = line.split()[0]
            pupver = (verstring, revision)

        return pupver

    def _build_revision(self, ud, d, name):
        return ud.revisions[name]

    def gitpkgv_revision(self, ud, d, name):
        """
        Return a sortable revision number by counting commits in the history
        Based on gitpkgv.bblass in meta-openembedded
        """
        rev = self._build_revision(ud, d, name)
        localpath = ud.localpath
        rev_file = os.path.join(localpath, "oe-gitpkgv_" + rev)
        if not os.path.exists(localpath):
            commits = None
        else:
            if not os.path.exists(rev_file) or not os.path.getsize(rev_file):
                from pipes import quote
                commits = bb.fetch2.runfetchcmd(
                        "git rev-list %s -- | wc -l" % (quote(rev)),
                        d, quiet=True).strip().lstrip('0')
                if commits:
                    open(rev_file, "w").write("%d\n" % int(commits))
            else:
                commits = open(rev_file, "r").readline(128).strip()
        if commits:
            return False, "%s+%s" % (commits, rev[:7])
        else:
            return True, str(rev)

    def checkstatus(self, fetch, ud, d):
        try:
            self._lsremote(ud, d, "")
            return True
        except FetchError:
            return False
