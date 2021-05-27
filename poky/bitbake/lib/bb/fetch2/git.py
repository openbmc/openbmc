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

- usehead
   For local git:// urls to use the current branch HEAD as the revision for use with
   AUTOREV. Implies nobranch.

"""

# Copyright (C) 2005 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import collections
import errno
import fnmatch
import os
import re
import subprocess
import tempfile
import bb
import bb.progress
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger


class GitProgressHandler(bb.progress.LineFilterProgressHandler):
    """Extract progress information from git output"""
    def __init__(self, d):
        self._buffer = ''
        self._count = 0
        super(GitProgressHandler, self).__init__(d)
        # Send an initial progress event so the bar gets shown
        self._fire_progress(-1)

    def write(self, string):
        self._buffer += string
        stages = ['Counting objects', 'Compressing objects', 'Receiving objects', 'Resolving deltas']
        stage_weights = [0.2, 0.05, 0.5, 0.25]
        stagenum = 0
        for i, stage in reversed(list(enumerate(stages))):
            if stage in self._buffer:
                stagenum = i
                self._buffer = ''
                break
        self._status = stages[stagenum]
        percs = re.findall(r'(\d+)%', string)
        if percs:
            progress = int(round((int(percs[-1]) * stage_weights[stagenum]) + (sum(stage_weights[:stagenum]) * 100)))
            rates = re.findall(r'([\d.]+ [a-zA-Z]*/s+)', string)
            if rates:
                rate = rates[-1]
            else:
                rate = None
            self.update(progress, rate)
        else:
            if stagenum == 0:
                percs = re.findall(r': (\d+)', string)
                if percs:
                    count = int(percs[-1])
                    if count > self._count:
                        self._count = count
                        self._fire_progress(-count)
        super(GitProgressHandler, self).write(string)


class Git(FetchMethod):
    bitbake_dir = os.path.abspath(os.path.join(os.path.dirname(os.path.join(os.path.abspath(__file__))), '..', '..', '..'))
    make_shallow_path = os.path.join(bitbake_dir, 'bin', 'git-make-shallow')

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

        # usehead implies nobranch
        ud.usehead = ud.parm.get("usehead","0") == "1"
        if ud.usehead:
            if ud.proto != "file":
                 raise bb.fetch2.ParameterError("The usehead option is only for use with local ('protocol=file') git repositories", ud.url)
            ud.nobranch = 1

        # bareclone implies nocheckout
        ud.bareclone = ud.parm.get("bareclone","0") == "1"
        if ud.bareclone:
            ud.nocheckout = 1
  
        ud.unresolvedrev = {}
        branches = ud.parm.get("branch", "master").split(',')
        if len(branches) != len(ud.names):
            raise bb.fetch2.ParameterError("The number of name and branch parameters is not balanced", ud.url)

        ud.cloneflags = "-s -n"
        if ud.bareclone:
            ud.cloneflags += " --mirror"

        ud.shallow = d.getVar("BB_GIT_SHALLOW") == "1"
        ud.shallow_extra_refs = (d.getVar("BB_GIT_SHALLOW_EXTRA_REFS") or "").split()

        depth_default = d.getVar("BB_GIT_SHALLOW_DEPTH")
        if depth_default is not None:
            try:
                depth_default = int(depth_default or 0)
            except ValueError:
                raise bb.fetch2.FetchError("Invalid depth for BB_GIT_SHALLOW_DEPTH: %s" % depth_default)
            else:
                if depth_default < 0:
                    raise bb.fetch2.FetchError("Invalid depth for BB_GIT_SHALLOW_DEPTH: %s" % depth_default)
        else:
            depth_default = 1
        ud.shallow_depths = collections.defaultdict(lambda: depth_default)

        revs_default = d.getVar("BB_GIT_SHALLOW_REVS")
        ud.shallow_revs = []
        ud.branches = {}
        for pos, name in enumerate(ud.names):
            branch = branches[pos]
            ud.branches[name] = branch
            ud.unresolvedrev[name] = branch

            shallow_depth = d.getVar("BB_GIT_SHALLOW_DEPTH_%s" % name)
            if shallow_depth is not None:
                try:
                    shallow_depth = int(shallow_depth or 0)
                except ValueError:
                    raise bb.fetch2.FetchError("Invalid depth for BB_GIT_SHALLOW_DEPTH_%s: %s" % (name, shallow_depth))
                else:
                    if shallow_depth < 0:
                        raise bb.fetch2.FetchError("Invalid depth for BB_GIT_SHALLOW_DEPTH_%s: %s" % (name, shallow_depth))
                    ud.shallow_depths[name] = shallow_depth

            revs = d.getVar("BB_GIT_SHALLOW_REVS_%s" % name)
            if revs is not None:
                ud.shallow_revs.extend(revs.split())
            elif revs_default is not None:
                ud.shallow_revs.extend(revs_default.split())

        if (ud.shallow and
                not ud.shallow_revs and
                all(ud.shallow_depths[n] == 0 for n in ud.names)):
            # Shallow disabled for this URL
            ud.shallow = False

        if ud.usehead:
            ud.unresolvedrev['default'] = 'HEAD'

        ud.basecmd = d.getVar("FETCHCMD_git") or "git -c core.fsyncobjectfiles=0"

        write_tarballs = d.getVar("BB_GENERATE_MIRROR_TARBALLS") or "0"
        ud.write_tarballs = write_tarballs != "0" or ud.rebaseable
        ud.write_shallow_tarballs = (d.getVar("BB_GENERATE_SHALLOW_TARBALLS") or write_tarballs) != "0"

        ud.setup_revisions(d)

        for name in ud.names:
            # Ensure anything that doesn't look like a sha256 checksum/revision is translated into one
            if not ud.revisions[name] or len(ud.revisions[name]) != 40  or (False in [c in "abcdef0123456789" for c in ud.revisions[name]]):
                if ud.revisions[name]:
                    ud.unresolvedrev[name] = ud.revisions[name]
                ud.revisions[name] = self.latest_revision(ud, d, name)

        gitsrcname = '%s%s' % (ud.host.replace(':', '.'), ud.path.replace('/', '.').replace('*', '.').replace(' ','_'))
        if gitsrcname.startswith('.'):
            gitsrcname = gitsrcname[1:]

        # for rebaseable git repo, it is necessary to keep mirror tar ball
        # per revision, so that even the revision disappears from the
        # upstream repo in the future, the mirror will remain intact and still
        # contains the revision
        if ud.rebaseable:
            for name in ud.names:
                gitsrcname = gitsrcname + '_' + ud.revisions[name]

        dl_dir = d.getVar("DL_DIR")
        gitdir = d.getVar("GITDIR") or (dl_dir + "/git2")
        ud.clonedir = os.path.join(gitdir, gitsrcname)
        ud.localfile = ud.clonedir

        mirrortarball = 'git2_%s.tar.gz' % gitsrcname
        ud.fullmirror = os.path.join(dl_dir, mirrortarball)
        ud.mirrortarballs = [mirrortarball]
        if ud.shallow:
            tarballname = gitsrcname
            if ud.bareclone:
                tarballname = "%s_bare" % tarballname

            if ud.shallow_revs:
                tarballname = "%s_%s" % (tarballname, "_".join(sorted(ud.shallow_revs)))

            for name, revision in sorted(ud.revisions.items()):
                tarballname = "%s_%s" % (tarballname, ud.revisions[name][:7])
                depth = ud.shallow_depths[name]
                if depth:
                    tarballname = "%s-%s" % (tarballname, depth)

            shallow_refs = []
            if not ud.nobranch:
                shallow_refs.extend(ud.branches.values())
            if ud.shallow_extra_refs:
                shallow_refs.extend(r.replace('refs/heads/', '').replace('*', 'ALL') for r in ud.shallow_extra_refs)
            if shallow_refs:
                tarballname = "%s_%s" % (tarballname, "_".join(sorted(shallow_refs)).replace('/', '.'))

            fetcher = self.__class__.__name__.lower()
            ud.shallowtarball = '%sshallow_%s.tar.gz' % (fetcher, tarballname)
            ud.fullshallow = os.path.join(dl_dir, ud.shallowtarball)
            ud.mirrortarballs.insert(0, ud.shallowtarball)

    def localpath(self, ud, d):
        return ud.clonedir

    def need_update(self, ud, d):
        return self.clonedir_need_update(ud, d) or self.shallow_tarball_need_update(ud) or self.tarball_need_update(ud)

    def clonedir_need_update(self, ud, d):
        if not os.path.exists(ud.clonedir):
            return True
        if ud.shallow and ud.write_shallow_tarballs and self.clonedir_need_shallow_revs(ud, d):
            return True
        for name in ud.names:
            if not self._contains_ref(ud, d, name, ud.clonedir):
                return True
        return False

    def clonedir_need_shallow_revs(self, ud, d):
        for rev in ud.shallow_revs:
            try:
                runfetchcmd('%s rev-parse -q --verify %s' % (ud.basecmd, rev), d, quiet=True, workdir=ud.clonedir)
            except bb.fetch2.FetchError:
                return rev
        return None

    def shallow_tarball_need_update(self, ud):
        return ud.shallow and ud.write_shallow_tarballs and not os.path.exists(ud.fullshallow)

    def tarball_need_update(self, ud):
        return ud.write_tarballs and not os.path.exists(ud.fullmirror)

    def try_premirror(self, ud, d):
        # If we don't do this, updating an existing checkout with only premirrors
        # is not possible
        if bb.utils.to_boolean(d.getVar("BB_FETCH_PREMIRRORONLY")):
            return True
        if os.path.exists(ud.clonedir):
            return False
        return True

    def download(self, ud, d):
        """Fetch url"""

        # A current clone is preferred to either tarball, a shallow tarball is
        # preferred to an out of date clone, and a missing clone will use
        # either tarball.
        if ud.shallow and os.path.exists(ud.fullshallow) and self.need_update(ud, d):
            ud.localpath = ud.fullshallow
            return
        elif os.path.exists(ud.fullmirror) and not os.path.exists(ud.clonedir):
            bb.utils.mkdirhier(ud.clonedir)
            runfetchcmd("tar -xzf %s" % ud.fullmirror, d, workdir=ud.clonedir)

        repourl = self._get_repo_url(ud)

        # If the repo still doesn't exist, fallback to cloning it
        if not os.path.exists(ud.clonedir):
            # We do this since git will use a "-l" option automatically for local urls where possible
            if repourl.startswith("file://"):
                repourl = repourl[7:]
            clone_cmd = "LANG=C %s clone --bare --mirror \"%s\" %s --progress" % (ud.basecmd, repourl, ud.clonedir)
            if ud.proto.lower() != 'file':
                bb.fetch2.check_network_access(d, clone_cmd, ud.url)
            progresshandler = GitProgressHandler(d)
            runfetchcmd(clone_cmd, d, log=progresshandler)

        # Update the checkout if needed
        if self.clonedir_need_update(ud, d):
            output = runfetchcmd("%s remote" % ud.basecmd, d, quiet=True, workdir=ud.clonedir)
            if "origin" in output:
              runfetchcmd("%s remote rm origin" % ud.basecmd, d, workdir=ud.clonedir)

            runfetchcmd("%s remote add --mirror=fetch origin \"%s\"" % (ud.basecmd, repourl), d, workdir=ud.clonedir)
            fetch_cmd = "LANG=C %s fetch -f --progress \"%s\" refs/*:refs/*" % (ud.basecmd, repourl)
            if ud.proto.lower() != 'file':
                bb.fetch2.check_network_access(d, fetch_cmd, ud.url)
            progresshandler = GitProgressHandler(d)
            runfetchcmd(fetch_cmd, d, log=progresshandler, workdir=ud.clonedir)
            runfetchcmd("%s prune-packed" % ud.basecmd, d, workdir=ud.clonedir)
            runfetchcmd("%s pack-refs --all" % ud.basecmd, d, workdir=ud.clonedir)
            runfetchcmd("%s pack-redundant --all | xargs -r rm" % ud.basecmd, d, workdir=ud.clonedir)
            try:
                os.unlink(ud.fullmirror)
            except OSError as exc:
                if exc.errno != errno.ENOENT:
                    raise

        for name in ud.names:
            if not self._contains_ref(ud, d, name, ud.clonedir):
                raise bb.fetch2.FetchError("Unable to find revision %s in branch %s even from upstream" % (ud.revisions[name], ud.branches[name]))

        if ud.shallow and ud.write_shallow_tarballs:
            missing_rev = self.clonedir_need_shallow_revs(ud, d)
            if missing_rev:
                raise bb.fetch2.FetchError("Unable to find revision %s even from upstream" % missing_rev)

        if self._contains_lfs(ud, d, ud.clonedir) and self._need_lfs(ud):
            # Unpack temporary working copy, use it to run 'git checkout' to force pre-fetching
            # of all LFS blobs needed at the the srcrev.
            #
            # It would be nice to just do this inline here by running 'git-lfs fetch'
            # on the bare clonedir, but that operation requires a working copy on some
            # releases of Git LFS.
            tmpdir = tempfile.mkdtemp(dir=d.getVar('DL_DIR'))
            try:
                # Do the checkout. This implicitly involves a Git LFS fetch.
                Git.unpack(self, ud, tmpdir, d)

                # Scoop up a copy of any stuff that Git LFS downloaded. Merge them into
                # the bare clonedir.
                #
                # As this procedure is invoked repeatedly on incremental fetches as
                # a recipe's SRCREV is bumped throughout its lifetime, this will
                # result in a gradual accumulation of LFS blobs in <ud.clonedir>/lfs
                # corresponding to all the blobs reachable from the different revs
                # fetched across time.
                #
                # Only do this if the unpack resulted in a .git/lfs directory being
                # created; this only happens if at least one blob needed to be
                # downloaded.
                if os.path.exists(os.path.join(tmpdir, "git", ".git", "lfs")):
                    runfetchcmd("tar -cf - lfs | tar -xf - -C %s" % ud.clonedir, d, workdir="%s/git/.git" % tmpdir)
            finally:
                bb.utils.remove(tmpdir, recurse=True)

    def build_mirror_data(self, ud, d):
        if ud.shallow and ud.write_shallow_tarballs:
            if not os.path.exists(ud.fullshallow):
                if os.path.islink(ud.fullshallow):
                    os.unlink(ud.fullshallow)
                tempdir = tempfile.mkdtemp(dir=d.getVar('DL_DIR'))
                shallowclone = os.path.join(tempdir, 'git')
                try:
                    self.clone_shallow_local(ud, shallowclone, d)

                    logger.info("Creating tarball of git repository")
                    runfetchcmd("tar -czf %s ." % ud.fullshallow, d, workdir=shallowclone)
                    runfetchcmd("touch %s.done" % ud.fullshallow, d)
                finally:
                    bb.utils.remove(tempdir, recurse=True)
        elif ud.write_tarballs and not os.path.exists(ud.fullmirror):
            if os.path.islink(ud.fullmirror):
                os.unlink(ud.fullmirror)

            logger.info("Creating tarball of git repository")
            runfetchcmd("tar -czf %s ." % ud.fullmirror, d, workdir=ud.clonedir)
            runfetchcmd("touch %s.done" % ud.fullmirror, d)

    def clone_shallow_local(self, ud, dest, d):
        """Clone the repo and make it shallow.

        The upstream url of the new clone isn't set at this time, as it'll be
        set correctly when unpacked."""
        runfetchcmd("%s clone %s %s %s" % (ud.basecmd, ud.cloneflags, ud.clonedir, dest), d)

        to_parse, shallow_branches = [], []
        for name in ud.names:
            revision = ud.revisions[name]
            depth = ud.shallow_depths[name]
            if depth:
                to_parse.append('%s~%d^{}' % (revision, depth - 1))

            # For nobranch, we need a ref, otherwise the commits will be
            # removed, and for non-nobranch, we truncate the branch to our
            # srcrev, to avoid keeping unnecessary history beyond that.
            branch = ud.branches[name]
            if ud.nobranch:
                ref = "refs/shallow/%s" % name
            elif ud.bareclone:
                ref = "refs/heads/%s" % branch
            else:
                ref = "refs/remotes/origin/%s" % branch

            shallow_branches.append(ref)
            runfetchcmd("%s update-ref %s %s" % (ud.basecmd, ref, revision), d, workdir=dest)

        # Map srcrev+depths to revisions
        parsed_depths = runfetchcmd("%s rev-parse %s" % (ud.basecmd, " ".join(to_parse)), d, workdir=dest)

        # Resolve specified revisions
        parsed_revs = runfetchcmd("%s rev-parse %s" % (ud.basecmd, " ".join('"%s^{}"' % r for r in ud.shallow_revs)), d, workdir=dest)
        shallow_revisions = parsed_depths.splitlines() + parsed_revs.splitlines()

        # Apply extra ref wildcards
        all_refs = runfetchcmd('%s for-each-ref "--format=%%(refname)"' % ud.basecmd,
                               d, workdir=dest).splitlines()
        for r in ud.shallow_extra_refs:
            if not ud.bareclone:
                r = r.replace('refs/heads/', 'refs/remotes/origin/')

            if '*' in r:
                matches = filter(lambda a: fnmatch.fnmatchcase(a, r), all_refs)
                shallow_branches.extend(matches)
            else:
                shallow_branches.append(r)

        # Make the repository shallow
        shallow_cmd = [self.make_shallow_path, '-s']
        for b in shallow_branches:
            shallow_cmd.append('-r')
            shallow_cmd.append(b)
        shallow_cmd.extend(shallow_revisions)
        runfetchcmd(subprocess.list2cmdline(shallow_cmd), d, workdir=dest)

    def unpack(self, ud, destdir, d):
        """ unpack the downloaded src to destdir"""

        subdir = ud.parm.get("subpath", "")
        if subdir != "":
            readpathspec = ":%s" % subdir
            def_destsuffix = "%s/" % os.path.basename(subdir.rstrip('/'))
        else:
            readpathspec = ""
            def_destsuffix = "git/"

        destsuffix = ud.parm.get("destsuffix", def_destsuffix)
        destdir = ud.destdir = os.path.join(destdir, destsuffix)
        if os.path.exists(destdir):
            bb.utils.prunedir(destdir)

        need_lfs = self._need_lfs(ud)

        if not need_lfs:
            ud.basecmd = "GIT_LFS_SKIP_SMUDGE=1 " + ud.basecmd

        source_found = False
        source_error = []

        if not source_found:
            clonedir_is_up_to_date = not self.clonedir_need_update(ud, d)
            if clonedir_is_up_to_date:
                runfetchcmd("%s clone %s %s/ %s" % (ud.basecmd, ud.cloneflags, ud.clonedir, destdir), d)
                source_found = True
            else:
                source_error.append("clone directory not available or not up to date: " + ud.clonedir)

        if not source_found:
            if ud.shallow:
                if os.path.exists(ud.fullshallow):
                    bb.utils.mkdirhier(destdir)
                    runfetchcmd("tar -xzf %s" % ud.fullshallow, d, workdir=destdir)
                    source_found = True
                else:
                    source_error.append("shallow clone not available: " + ud.fullshallow)
            else:
                source_error.append("shallow clone not enabled")

        if not source_found:
            raise bb.fetch2.UnpackError("No up to date source found: " + "; ".join(source_error), ud.url)

        repourl = self._get_repo_url(ud)
        runfetchcmd("%s remote set-url origin \"%s\"" % (ud.basecmd, repourl), d, workdir=destdir)

        if self._contains_lfs(ud, d, destdir):
            if need_lfs and not self._find_git_lfs(d):
                raise bb.fetch2.FetchError("Repository %s has LFS content, install git-lfs on host to download (or set lfs=0 to ignore it)" % (repourl))
            elif not need_lfs:
                bb.note("Repository %s has LFS content but it is not being fetched" % (repourl))

        if not ud.nocheckout:
            if subdir != "":
                runfetchcmd("%s read-tree %s%s" % (ud.basecmd, ud.revisions[ud.names[0]], readpathspec), d,
                            workdir=destdir)
                runfetchcmd("%s checkout-index -q -f -a" % ud.basecmd, d, workdir=destdir)
            elif not ud.nobranch:
                branchname =  ud.branches[ud.names[0]]
                runfetchcmd("%s checkout -B %s %s" % (ud.basecmd, branchname, \
                            ud.revisions[ud.names[0]]), d, workdir=destdir)
                runfetchcmd("%s branch %s --set-upstream-to origin/%s" % (ud.basecmd, branchname, \
                            branchname), d, workdir=destdir)
            else:
                runfetchcmd("%s checkout %s" % (ud.basecmd, ud.revisions[ud.names[0]]), d, workdir=destdir)

        return True

    def clean(self, ud, d):
        """ clean the git directory """

        to_remove = [ud.localpath, ud.fullmirror, ud.fullmirror + ".done"]
        # The localpath is a symlink to clonedir when it is cloned from a
        # mirror, so remove both of them.
        if os.path.islink(ud.localpath):
            clonedir = os.path.realpath(ud.localpath)
            to_remove.append(clonedir)

        for r in to_remove:
            if os.path.exists(r):
                bb.note('Removing %s' % r)
                bb.utils.remove(r, True)

    def supports_srcrev(self):
        return True

    def _contains_ref(self, ud, d, name, wd):
        cmd = ""
        if ud.nobranch:
            cmd = "%s log --pretty=oneline -n 1 %s -- 2> /dev/null | wc -l" % (
                ud.basecmd, ud.revisions[name])
        else:
            cmd =  "%s branch --contains %s --list %s 2> /dev/null | wc -l" % (
                ud.basecmd, ud.revisions[name], ud.branches[name])
        try:
            output = runfetchcmd(cmd, d, quiet=True, workdir=wd)
        except bb.fetch2.FetchError:
            return False
        if len(output.split()) > 1:
            raise bb.fetch2.FetchError("The command '%s' gave output with more then 1 line unexpectedly, output: '%s'" % (cmd, output))
        return output.split()[0] != "0"

    def _need_lfs(self, ud):
        return ud.parm.get("lfs", "1") == "1"

    def _contains_lfs(self, ud, d, wd):
        """
        Check if the repository has 'lfs' (large file) content
        """

        if not ud.nobranch:
            branchname = ud.branches[ud.names[0]]
        else:
            branchname = "master"

        # The bare clonedir doesn't use the remote names; it has the branch immediately.
        if wd == ud.clonedir:
            refname = ud.branches[ud.names[0]]
        else:
            refname = "origin/%s" % ud.branches[ud.names[0]]

        cmd = "%s grep lfs %s:.gitattributes | wc -l" % (
            ud.basecmd, refname)

        try:
            output = runfetchcmd(cmd, d, quiet=True, workdir=wd)
            if int(output) > 0:
                return True
        except (bb.fetch2.FetchError,ValueError):
            pass
        return False

    def _find_git_lfs(self, d):
        """
        Return True if git-lfs can be found, False otherwise.
        """
        import shutil
        return shutil.which("git-lfs", path=d.getVar('PATH')) is not None

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
        # Collapse adjacent slashes
        slash_re = re.compile(r"/+")
        return "git:" + ud.host + slash_re.sub(".", ud.path) + ud.unresolvedrev[name]

    def _lsremote(self, ud, d, search):
        """
        Run git ls-remote with the specified search string
        """
        # Prevent recursion e.g. in OE if SRCPV is in PV, PV is in WORKDIR,
        # and WORKDIR is in PATH (as a result of RSS), our call to
        # runfetchcmd() exports PATH so this function will get called again (!)
        # In this scenario the return call of the function isn't actually
        # important - WORKDIR isn't needed in PATH to call git ls-remote
        # anyway.
        if d.getVar('_BB_GIT_IN_LSREMOTE', False):
            return ''
        d.setVar('_BB_GIT_IN_LSREMOTE', '1')
        try:
            repourl = self._get_repo_url(ud)
            cmd = "%s ls-remote \"%s\" %s" % \
                (ud.basecmd, repourl, search)
            if ud.proto.lower() != 'file':
                bb.fetch2.check_network_access(d, cmd, repourl)
            output = runfetchcmd(cmd, d, True)
            if not output:
                raise bb.fetch2.FetchError("The command %s gave empty output unexpectedly" % cmd, ud.url)
        finally:
            d.delVar('_BB_GIT_IN_LSREMOTE')
        return output

    def _latest_revision(self, ud, d, name):
        """
        Compute the HEAD revision for the url
        """
        output = self._lsremote(ud, d, "")
        # Tags of the form ^{} may not work, need to fallback to other form
        if ud.unresolvedrev[name][:5] == "refs/" or ud.usehead:
            head = ud.unresolvedrev[name]
            tag = ud.unresolvedrev[name]
        else:
            head = "refs/heads/%s" % ud.unresolvedrev[name]
            tag = "refs/tags/%s" % ud.unresolvedrev[name]
        for s in [head, tag + "^{}", tag]:
            for l in output.strip().split('\n'):
                sha1, ref = l.split()
                if s == ref:
                    return sha1
        raise bb.fetch2.FetchError("Unable to resolve '%s' in upstream git repository in git ls-remote output for %s" % \
            (ud.unresolvedrev[name], ud.host+ud.path))

    def latest_versionstring(self, ud, d):
        """
        Compute the latest release name like "x.y.x" in "x.y.x+gitHASH"
        by searching through the tags output of ls-remote, comparing
        versions and returning the highest match.
        """
        pupver = ('', '')

        tagregex = re.compile(d.getVar('UPSTREAM_CHECK_GITTAGREGEX') or r"(?P<pver>([0-9][\.|_]?)+)")
        try:
            output = self._lsremote(ud, d, "refs/tags/*")
        except (bb.fetch2.FetchError, bb.fetch2.NetworkAccess) as e:
            bb.note("Could not list remote: %s" % str(e))
            return pupver

        verstring = ""
        revision = ""
        for line in output.split("\n"):
            if not line:
                break

            tag_head = line.split("/")[-1]
            # Ignore non-released branches
            m = re.search(r"(alpha|beta|rc|final)+", tag_head)
            if m:
                continue

            # search for version in the line
            tag = tagregex.search(tag_head)
            if tag is None:
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
                        "git rev-list %s -- | wc -l" % quote(rev),
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
        except bb.fetch2.FetchError:
            return False
