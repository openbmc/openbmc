"""
BitBake 'Fetch' implementation for bzr.

"""

# Copyright (C) 2007 Ross Burton
# Copyright (C) 2007 Richard Purdie
#
#   Classes for obtaining upstream sources for the
#   BitBake build tools.
#   Copyright (C) 2003, 2004  Chris Larson
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
import sys
import logging
import bb
from bb.fetch2 import FetchMethod
from bb.fetch2 import FetchError
from bb.fetch2 import runfetchcmd
from bb.fetch2 import logger

class Bzr(FetchMethod):
    def supports(self, ud, d):
        return ud.type in ['bzr']

    def urldata_init(self, ud, d):
        """
        init bzr specific variable within url data
        """
        # Create paths to bzr checkouts
        relpath = self._strip_leading_slashes(ud.path)
        ud.pkgdir = os.path.join(d.expand('${BZRDIR}'), ud.host, relpath)

        ud.setup_revisions(d)

        if not ud.revision:
            ud.revision = self.latest_revision(ud, d)

        ud.localfile = d.expand('bzr_%s_%s_%s.tar.gz' % (ud.host, ud.path.replace('/', '.'), ud.revision))

    def _buildbzrcommand(self, ud, d, command):
        """
        Build up an bzr commandline based on ud
        command is "fetch", "update", "revno"
        """

        basecmd = d.expand('${FETCHCMD_bzr}')

        proto =  ud.parm.get('protocol', 'http')

        bzrroot = ud.host + ud.path

        options = []

        if command == "revno":
            bzrcmd = "%s revno %s %s://%s" % (basecmd, " ".join(options), proto, bzrroot)
        else:
            if ud.revision:
                options.append("-r %s" % ud.revision)

            if command == "fetch":
                bzrcmd = "%s branch %s %s://%s" % (basecmd, " ".join(options), proto, bzrroot)
            elif command == "update":
                bzrcmd = "%s pull %s --overwrite" % (basecmd, " ".join(options))
            else:
                raise FetchError("Invalid bzr command %s" % command, ud.url)

        return bzrcmd

    def download(self, ud, d):
        """Fetch url"""

        if os.access(os.path.join(ud.pkgdir, os.path.basename(ud.pkgdir), '.bzr'), os.R_OK):
            bzrcmd = self._buildbzrcommand(ud, d, "update")
            logger.debug(1, "BZR Update %s", ud.url)
            bb.fetch2.check_network_access(d, bzrcmd, ud.url)
            runfetchcmd(bzrcmd, d, workdir=os.path.join(ud.pkgdir, os.path.basename(ud.path)))
        else:
            bb.utils.remove(os.path.join(ud.pkgdir, os.path.basename(ud.pkgdir)), True)
            bzrcmd = self._buildbzrcommand(ud, d, "fetch")
            bb.fetch2.check_network_access(d, bzrcmd, ud.url)
            logger.debug(1, "BZR Checkout %s", ud.url)
            bb.utils.mkdirhier(ud.pkgdir)
            logger.debug(1, "Running %s", bzrcmd)
            runfetchcmd(bzrcmd, d, workdir=ud.pkgdir)

        scmdata = ud.parm.get("scmdata", "")
        if scmdata == "keep":
            tar_flags = ""
        else:
            tar_flags = "--exclude='.bzr' --exclude='.bzrtags'"

        # tar them up to a defined filename
        runfetchcmd("tar %s -czf %s %s" % (tar_flags, ud.localpath, os.path.basename(ud.pkgdir)),
                    d, cleanup=[ud.localpath], workdir=ud.pkgdir)

    def supports_srcrev(self):
        return True

    def _revision_key(self, ud, d, name):
        """
        Return a unique key for the url
        """
        return "bzr:" + ud.pkgdir

    def _latest_revision(self, ud, d, name):
        """
        Return the latest upstream revision number
        """
        logger.debug(2, "BZR fetcher hitting network for %s", ud.url)

        bb.fetch2.check_network_access(d, self._buildbzrcommand(ud, d, "revno"), ud.url)

        output = runfetchcmd(self._buildbzrcommand(ud, d, "revno"), d, True)

        return output.strip()

    def sortable_revision(self, ud, d, name):
        """
        Return a sortable revision number which in our case is the revision number
        """

        return False, self._build_revision(ud, d)

    def _build_revision(self, ud, d):
        return ud.revision
