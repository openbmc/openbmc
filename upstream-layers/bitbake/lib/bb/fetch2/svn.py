"""
BitBake 'Fetch' implementation for svn.

"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2004        Marcin Juszkiewicz
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import bb
import re
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

        ud.basecmd = d.getVar("FETCHCMD_svn") or "/usr/bin/env svn --non-interactive --trust-server-cert"

        ud.module = ud.parm["module"]

        if not "path_spec" in ud.parm:
            ud.path_spec = ud.module
        else:
            ud.path_spec = ud.parm["path_spec"]

        # Create paths to svn checkouts
        svndir = d.getVar("SVNDIR") or (d.getVar("DL_DIR") + "/svn")
        relpath = self._strip_leading_slashes(ud.path)
        ud.pkgdir = os.path.join(svndir, ud.host, relpath)
        ud.moddir = os.path.join(ud.pkgdir, ud.path_spec)
        # Protects the repository from concurrent updates, e.g. from two
        # recipes fetching different revisions at the same time
        ud.svnlock = os.path.join(ud.pkgdir, "svn.lock")

        ud.setup_revisions(d)

        if 'rev' in ud.parm:
            ud.revision = ud.parm['rev']

        # Whether to use the @REV peg-revision syntax in the svn command or not
        ud.pegrevision = True
        if 'nopegrevision' in ud.parm:
            ud.pegrevision = False

        ud.localfile = d.expand('%s_%s_%s_%s_%s.tar.gz' % (ud.module.replace('/', '.'), ud.host, ud.path.replace('/', '.'), ud.revision, ["0", "1"][ud.pegrevision]))

    def _buildsvncommand(self, ud, d, command):
        """
        Build up an svn commandline based on ud
        command is "fetch", "update", "info"
        """

        proto = ud.parm.get('protocol', 'svn')

        svn_ssh = None
        if proto == "svn+ssh" and "ssh" in ud.parm:
            svn_ssh = ud.parm["ssh"]

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
            svncmd = "%s log --limit 1 --quiet %s %s://%s/%s/" % (ud.basecmd, " ".join(options), proto, svnroot, ud.module)
        else:
            suffix = ""

            # externals may be either 'allowed' or 'nowarn', but not both.  Allowed
            # will not issue a warning, but will log to the debug buffer what has likely
            # been downloaded by SVN.
            if not ("externals" in ud.parm and ud.parm["externals"] == "allowed"):
                options.append("--ignore-externals")

            if ud.revision:
                options.append("-r %s" % ud.revision)
                if ud.pegrevision:
                    suffix = "@%s" % (ud.revision)

            if command == "fetch":
                transportuser = ud.parm.get("transportuser", "")
                svncmd = "%s co %s %s://%s%s/%s%s %s" % (ud.basecmd, " ".join(options), proto, transportuser, svnroot, ud.module, suffix, ud.path_spec)
            elif command == "update":
                svncmd = "%s update %s" % (ud.basecmd, " ".join(options))
            else:
                raise FetchError("Invalid svn command %s" % command, ud.url)

        if svn_ssh:
            svncmd = "SVN_SSH=\"%s\" %s" % (svn_ssh, svncmd)

        return svncmd

    def download(self, ud, d):
        """Fetch url"""

        logger.debug2("Fetch: checking for module directory '" + ud.moddir + "'")

        lf = bb.utils.lockfile(ud.svnlock)

        try:
            if os.access(os.path.join(ud.moddir, '.svn'), os.R_OK):
                svncmd = self._buildsvncommand(ud, d, "update")
                logger.info("Update " + ud.url)
                # We need to attempt to run svn upgrade first in case its an older working format
                try:
                    runfetchcmd(ud.basecmd + " upgrade", d, workdir=ud.moddir)
                except FetchError:
                    pass
                logger.debug("Running %s", svncmd)
                bb.fetch2.check_network_access(d, svncmd, ud.url)
                runfetchcmd(svncmd, d, workdir=ud.moddir)
            else:
                svncmd = self._buildsvncommand(ud, d, "fetch")
                logger.info("Fetch " + ud.url)
                # check out sources there
                bb.utils.mkdirhier(ud.pkgdir)
                logger.debug("Running %s", svncmd)
                bb.fetch2.check_network_access(d, svncmd, ud.url)
                runfetchcmd(svncmd, d, workdir=ud.pkgdir)

            if not ("externals" in ud.parm and ud.parm["externals"] == "nowarn"):
                # Warn the user if this had externals (won't catch them all)
                output = runfetchcmd("svn propget svn:externals || true", d, workdir=ud.moddir)
                if output:
                    if "--ignore-externals" in svncmd.split():
                        bb.warn("%s contains svn:externals." % ud.url)
                        bb.warn("These should be added to the recipe SRC_URI as necessary.")
                        bb.warn("svn fetch has ignored externals:\n%s" % output)
                        bb.warn("To disable this warning add ';externals=nowarn' to the url.")
                    else:
                        bb.debug(1, "svn repository has externals:\n%s" % output)

            scmdata = ud.parm.get("scmdata", "")
            if scmdata == "keep":
                tar_flags = ""
            else:
                tar_flags = "--exclude='.svn'"

            # tar them up to a defined filename
            runfetchcmd("tar %s -czf %s %s" % (tar_flags, ud.localpath, ud.path_spec), d,
                        cleanup=[ud.localpath], workdir=ud.pkgdir)
        finally:
            bb.utils.unlockfile(lf)

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
        bb.fetch2.check_network_access(d, self._buildsvncommand(ud, d, "log1"), ud.url)

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

    def supports_checksum(self, urldata):
        return False
