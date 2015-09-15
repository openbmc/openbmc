# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' implementation for svn.

"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2004        Marcin Juszkiewicz
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
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import sys
import logging
import bb
import re
from   bb import data
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import FetchError
from   bb.fetch2 import MissingParameterError
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger

class Svn(FetchMethod):
    """Class to fetch a module or modules from svn repositories"""
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with svn.
        """
        return ud.type in ['svn']

    def urldata_init(self, ud, d):
        """
        init svn specific variable within url data
        """
        if not "module" in ud.parm:
            raise MissingParameterError('module', ud.url)

        ud.basecmd = d.getVar('FETCHCMD_svn', True)

        ud.module = ud.parm["module"]

        # Create paths to svn checkouts
        relpath = self._strip_leading_slashes(ud.path)
        ud.pkgdir = os.path.join(data.expand('${SVNDIR}', d), ud.host, relpath)
        ud.moddir = os.path.join(ud.pkgdir, ud.module)

        ud.setup_revisons(d)

        if 'rev' in ud.parm:
            ud.revision = ud.parm['rev']

        ud.localfile = data.expand('%s_%s_%s_%s_.tar.gz' % (ud.module.replace('/', '.'), ud.host, ud.path.replace('/', '.'), ud.revision), d)

    def _buildsvncommand(self, ud, d, command):
        """
        Build up an svn commandline based on ud
        command is "fetch", "update", "info"
        """

        proto = ud.parm.get('protocol', 'svn')

        svn_rsh = None
        if proto == "svn+ssh" and "rsh" in ud.parm:
            svn_rsh = ud.parm["rsh"]

        svnroot = ud.host + ud.path

        options = []

        options.append("--no-auth-cache")

        if ud.user:
            options.append("--username %s" % ud.user)

        if ud.pswd:
            options.append("--password %s" % ud.pswd)

        if command == "info":
            svncmd = "%s info %s %s://%s/%s/" % (ud.basecmd, " ".join(options), proto, svnroot, ud.module)
        elif command == "log1":
            svncmd = "%s log --limit 1 %s %s://%s/%s/" % (ud.basecmd, " ".join(options), proto, svnroot, ud.module)
        else:
            suffix = ""
            if ud.revision:
                options.append("-r %s" % ud.revision)
                suffix = "@%s" % (ud.revision)

            if command == "fetch":
                transportuser = ud.parm.get("transportuser", "")
                svncmd = "%s co %s %s://%s%s/%s%s %s" % (ud.basecmd, " ".join(options), proto, transportuser, svnroot, ud.module, suffix, ud.module)
            elif command == "update":
                svncmd = "%s update %s" % (ud.basecmd, " ".join(options))
            else:
                raise FetchError("Invalid svn command %s" % command, ud.url)

        if svn_rsh:
            svncmd = "svn_RSH=\"%s\" %s" % (svn_rsh, svncmd)

        return svncmd

    def download(self, ud, d):
        """Fetch url"""

        logger.debug(2, "Fetch: checking for module directory '" + ud.moddir + "'")

        if os.access(os.path.join(ud.moddir, '.svn'), os.R_OK):
            svnupdatecmd = self._buildsvncommand(ud, d, "update")
            logger.info("Update " + ud.url)
            # update sources there
            os.chdir(ud.moddir)
            # We need to attempt to run svn upgrade first in case its an older working format
            try:
                runfetchcmd(ud.basecmd + " upgrade", d)
            except FetchError:
                pass
            logger.debug(1, "Running %s", svnupdatecmd)
            bb.fetch2.check_network_access(d, svnupdatecmd, ud.url)
            runfetchcmd(svnupdatecmd, d)
        else:
            svnfetchcmd = self._buildsvncommand(ud, d, "fetch")
            logger.info("Fetch " + ud.url)
            # check out sources there
            bb.utils.mkdirhier(ud.pkgdir)
            os.chdir(ud.pkgdir)
            logger.debug(1, "Running %s", svnfetchcmd)
            bb.fetch2.check_network_access(d, svnfetchcmd, ud.url)
            runfetchcmd(svnfetchcmd, d)

        scmdata = ud.parm.get("scmdata", "")
        if scmdata == "keep":
            tar_flags = ""
        else:
            tar_flags = "--exclude '.svn'"

        os.chdir(ud.pkgdir)
        # tar them up to a defined filename
        runfetchcmd("tar %s -czf %s %s" % (tar_flags, ud.localpath, ud.module), d, cleanup = [ud.localpath])

    def clean(self, ud, d):
        """ Clean SVN specific files and dirs """

        bb.utils.remove(ud.localpath)
        bb.utils.remove(ud.moddir, True)
        

    def supports_srcrev(self):
        return True

    def _revision_key(self, ud, d, name):
        """
        Return a unique key for the url
        """
        return "svn:" + ud.moddir

    def _latest_revision(self, ud, d, name):
        """
        Return the latest upstream revision number
        """
        bb.fetch2.check_network_access(d, self._buildsvncommand(ud, d, "log1"))

        output = runfetchcmd("LANG=C LC_ALL=C " + self._buildsvncommand(ud, d, "log1"), d, True)

        # skip the first line, as per output of svn log
        # then we expect the revision on the 2nd line
        revision = re.search('^r([0-9]*)', output.splitlines()[1]).group(1)

        return revision

    def sortable_revision(self, ud, d, name):
        """
        Return a sortable revision number which in our case is the revision number
        """

        return False, self._build_revision(ud, d)

    def _build_revision(self, ud, d):
        return ud.revision
