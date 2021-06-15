"""
BitBake 'Fetch' git annex implementation
"""

# Copyright (C) 2014 Otavio Salvador
# Copyright (C) 2014 O.S. Systems Software LTDA.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import bb
from   bb.fetch2.git import Git
from   bb.fetch2 import runfetchcmd

class GitANNEX(Git):
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with git.
        """
        return ud.type in ['gitannex']

    def urldata_init(self, ud, d):
        super(GitANNEX, self).urldata_init(ud, d)
        if ud.shallow:
            ud.shallow_extra_refs += ['refs/heads/git-annex', 'refs/heads/synced/*']

    def uses_annex(self, ud, d, wd):
        for name in ud.names:
            try:
                runfetchcmd("%s rev-list git-annex" % (ud.basecmd), d, quiet=True, workdir=wd)
                return True
            except bb.fetch.FetchError:
                pass

        return False

    def update_annex(self, ud, d, wd):
        try:
            runfetchcmd("%s annex get --all" % (ud.basecmd), d, quiet=True, workdir=wd)
        except bb.fetch.FetchError:
            return False
        runfetchcmd("chmod u+w -R %s/annex" % (ud.clonedir), d, quiet=True, workdir=wd)

        return True

    def download(self, ud, d):
        Git.download(self, ud, d)

        if not ud.shallow or ud.localpath != ud.fullshallow:
            if self.uses_annex(ud, d, ud.clonedir):
                self.update_annex(ud, d, ud.clonedir)

    def clone_shallow_local(self, ud, dest, d):
        super(GitANNEX, self).clone_shallow_local(ud, dest, d)

        try:
            runfetchcmd("%s annex init" % ud.basecmd, d, workdir=dest)
        except bb.fetch.FetchError:
            pass

        if self.uses_annex(ud, d, dest):
            runfetchcmd("%s annex get" % ud.basecmd, d, workdir=dest)
            runfetchcmd("chmod u+w -R %s/.git/annex" % (dest), d, quiet=True, workdir=dest)

    def unpack(self, ud, destdir, d):
        Git.unpack(self, ud, destdir, d)

        try:
            runfetchcmd("%s annex init" % (ud.basecmd), d, workdir=ud.destdir)
        except bb.fetch.FetchError:
            pass

        annex = self.uses_annex(ud, d, ud.destdir)
        if annex:
            runfetchcmd("%s annex get" % (ud.basecmd), d, workdir=ud.destdir)
            runfetchcmd("chmod u+w -R %s/.git/annex" % (ud.destdir), d, quiet=True, workdir=ud.destdir)

