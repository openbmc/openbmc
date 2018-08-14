# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' git submodules implementation

Inherits from and extends the Git fetcher to retrieve submodules of a git repository
after cloning.

SRC_URI = "gitsm://<see Git fetcher for syntax>"

See the Git fetcher, git://, for usage documentation.

NOTE: Switching a SRC_URI from "git://" to "gitsm://" requires a clean of your recipe.

"""

# Copyright (C) 2013 Richard Purdie
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

import os
import bb
from   bb.fetch2.git import Git
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger

class GitSM(Git):
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with git.
        """
        return ud.type in ['gitsm']

    def uses_submodules(self, ud, d, wd):
        for name in ud.names:
            try:
                runfetchcmd("%s show %s:.gitmodules" % (ud.basecmd, ud.revisions[name]), d, quiet=True, workdir=wd)
                return True
            except bb.fetch.FetchError:
                pass
        return False

    def _set_relative_paths(self, repopath):
        """
        Fix submodule paths to be relative instead of absolute,
        so that when we move the repo it doesn't break
        (In Git 1.7.10+ this is done automatically)
        """
        submodules = []
        with open(os.path.join(repopath, '.gitmodules'), 'r') as f:
            for line in f.readlines():
                if line.startswith('[submodule'):
                    submodules.append(line.split('"')[1])

        for module in submodules:
            repo_conf = os.path.join(repopath, module, '.git')
            if os.path.exists(repo_conf):
                with open(repo_conf, 'r') as f:
                    lines = f.readlines()
                newpath = ''
                for i, line in enumerate(lines):
                    if line.startswith('gitdir:'):
                        oldpath = line.split(': ')[-1].rstrip()
                        if oldpath.startswith('/'):
                            newpath = '../' * (module.count('/') + 1) + '.git/modules/' + module
                            lines[i] = 'gitdir: %s\n' % newpath
                            break
                if newpath:
                    with open(repo_conf, 'w') as f:
                        for line in lines:
                            f.write(line)

            repo_conf2 = os.path.join(repopath, '.git', 'modules', module, 'config')
            if os.path.exists(repo_conf2):
                with open(repo_conf2, 'r') as f:
                    lines = f.readlines()
                newpath = ''
                for i, line in enumerate(lines):
                    if line.lstrip().startswith('worktree = '):
                        oldpath = line.split(' = ')[-1].rstrip()
                        if oldpath.startswith('/'):
                            newpath = '../' * (module.count('/') + 3) + module
                            lines[i] = '\tworktree = %s\n' % newpath
                            break
                if newpath:
                    with open(repo_conf2, 'w') as f:
                        for line in lines:
                            f.write(line)

    def update_submodules(self, ud, d):
        # We have to convert bare -> full repo, do the submodule bit, then convert back
        tmpclonedir = ud.clonedir + ".tmp"
        gitdir = tmpclonedir + os.sep + ".git"
        bb.utils.remove(tmpclonedir, True)
        os.mkdir(tmpclonedir)
        os.rename(ud.clonedir, gitdir)
        runfetchcmd("sed " + gitdir + "/config -i -e 's/bare.*=.*true/bare = false/'", d)
        runfetchcmd(ud.basecmd + " reset --hard", d, workdir=tmpclonedir)
        runfetchcmd(ud.basecmd + " checkout -f " + ud.revisions[ud.names[0]], d, workdir=tmpclonedir)
        runfetchcmd(ud.basecmd + " submodule update --init --recursive", d, workdir=tmpclonedir)
        self._set_relative_paths(tmpclonedir)
        runfetchcmd("sed " + gitdir + "/config -i -e 's/bare.*=.*false/bare = true/'", d, workdir=tmpclonedir)
        os.rename(gitdir, ud.clonedir,)
        bb.utils.remove(tmpclonedir, True)

    def download(self, ud, d):
        Git.download(self, ud, d)

        if not ud.shallow or ud.localpath != ud.fullshallow:
            submodules = self.uses_submodules(ud, d, ud.clonedir)
            if submodules:
                self.update_submodules(ud, d)

    def clone_shallow_local(self, ud, dest, d):
        super(GitSM, self).clone_shallow_local(ud, dest, d)

        runfetchcmd('cp -fpPRH "%s/modules" "%s/"' % (ud.clonedir, os.path.join(dest, '.git')), d)

    def unpack(self, ud, destdir, d):
        Git.unpack(self, ud, destdir, d)

        if self.uses_submodules(ud, d, ud.destdir):
            runfetchcmd(ud.basecmd + " checkout " + ud.revisions[ud.names[0]], d, workdir=ud.destdir)
            runfetchcmd(ud.basecmd + " submodule update --init --recursive", d, workdir=ud.destdir)
