# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' git annex implementation
"""

# Copyright (C) 2014 Otavio Salvador
# Copyright (C) 2014 O.S. Systems Software LTDA.
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
from   bb import data
from   bb.fetch2.git import Git
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger

class GitANNEX(Git):
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with git.
        """
        return ud.type in ['gitannex']

    def uses_annex(self, ud, d):
        for name in ud.names:
            try:
                runfetchcmd("%s rev-list git-annex" % (ud.basecmd), d, quiet=True)
                return True
            except bb.fetch.FetchError:
                pass

        return False

    def update_annex(self, ud, d):
        try:
            runfetchcmd("%s annex get --all" % (ud.basecmd), d, quiet=True)
        except bb.fetch.FetchError:
            return False
        runfetchcmd("chmod u+w -R %s/annex" % (ud.clonedir), d, quiet=True)

        return True

    def download(self, ud, d):
        Git.download(self, ud, d)

        os.chdir(ud.clonedir)
        annex = self.uses_annex(ud, d)
        if annex:
            self.update_annex(ud, d)

    def unpack(self, ud, destdir, d):
        Git.unpack(self, ud, destdir, d)

        os.chdir(ud.destdir)
        try:
            runfetchcmd("%s annex sync" % (ud.basecmd), d)
        except bb.fetch.FetchError:
            pass

        annex = self.uses_annex(ud, d)
        if annex:
            runfetchcmd("%s annex get" % (ud.basecmd), d)
            runfetchcmd("chmod u+w -R %s/.git/annex" % (ud.destdir), d, quiet=True)
