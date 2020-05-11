#
# SPDX-License-Identifier: GPL-2.0-only
#
"""
Bitbake "Fetch" implementation for osc (Opensuse build service client).
Based on the svn "Fetch" implementation.

"""

import logging
import  bb
from    bb.fetch2 import FetchMethod
from    bb.fetch2 import FetchError
from    bb.fetch2 import MissingParameterError
from    bb.fetch2 import runfetchcmd

class Osc(FetchMethod):
    """Class to fetch a module or modules from Opensuse build server
       repositories."""

    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with osc.
        """
        return ud.type in ['osc']

    def urldata_init(self, ud, d):
        if not "module" in ud.parm:
            raise MissingParameterError('module', ud.url)

        ud.module = ud.parm["module"]

        # Create paths to osc checkouts
        oscdir = d.getVar("OSCDIR") or (d.getVar("DL_DIR") + "/osc")
        relpath = self._strip_leading_slashes(ud.path)
        ud.pkgdir = os.path.join(oscdir, ud.host)
        ud.moddir = os.path.join(ud.pkgdir, relpath, ud.module)

        if 'rev' in ud.parm:
            ud.revision = ud.parm['rev']
        else:
            pv = d.getVar("PV", False)
            rev = bb.fetch2.srcrev_internal_helper(ud, d)
            if rev:
                ud.revision = rev
            else:
                ud.revision = ""

        ud.localfile = d.expand('%s_%s_%s.tar.gz' % (ud.module.replace('/', '.'), ud.path.replace('/', '.'), ud.revision))

    def _buildosccommand(self, ud, d, command):
        """
        Build up an ocs commandline based on ud
        command is "fetch", "update", "info"
        """

        basecmd = d.getVar("FETCHCMD_osc") or "/usr/bin/env osc"

        proto = ud.parm.get('protocol', 'ocs')

        options = []

        config = "-c %s" % self.generate_config(ud, d)

        if ud.revision:
            options.append("-r %s" % ud.revision)

        coroot = self._strip_leading_slashes(ud.path)

        if command == "fetch":
            osccmd = "%s %s co %s/%s %s" % (basecmd, config, coroot, ud.module, " ".join(options))
        elif command == "update":
            osccmd = "%s %s up %s" % (basecmd, config, " ".join(options))
        else:
            raise FetchError("Invalid osc command %s" % command, ud.url)

        return osccmd

    def download(self, ud, d):
        """
        Fetch url
        """

        logger.debug(2, "Fetch: checking for module directory '" + ud.moddir + "'")

        if os.access(os.path.join(d.getVar('OSCDIR'), ud.path, ud.module), os.R_OK):
            oscupdatecmd = self._buildosccommand(ud, d, "update")
            logger.info("Update "+ ud.url)
            # update sources there
            logger.debug(1, "Running %s", oscupdatecmd)
            bb.fetch2.check_network_access(d, oscupdatecmd, ud.url)
            runfetchcmd(oscupdatecmd, d, workdir=ud.moddir)
        else:
            oscfetchcmd = self._buildosccommand(ud, d, "fetch")
            logger.info("Fetch " + ud.url)
            # check out sources there
            bb.utils.mkdirhier(ud.pkgdir)
            logger.debug(1, "Running %s", oscfetchcmd)
            bb.fetch2.check_network_access(d, oscfetchcmd, ud.url)
            runfetchcmd(oscfetchcmd, d, workdir=ud.pkgdir)

        # tar them up to a defined filename
        runfetchcmd("tar -czf %s %s" % (ud.localpath, ud.module), d,
                    cleanup=[ud.localpath], workdir=os.path.join(ud.pkgdir + ud.path))

    def supports_srcrev(self):
        return False

    def generate_config(self, ud, d):
        """
        Generate a .oscrc to be used for this run.
        """

        config_path = os.path.join(d.getVar('OSCDIR'), "oscrc")
        if (os.path.exists(config_path)):
            os.remove(config_path)

        f = open(config_path, 'w')
        f.write("[general]\n")
        f.write("apisrv = %s\n" % ud.host)
        f.write("scheme = http\n")
        f.write("su-wrapper = su -c\n")
        f.write("build-root = %s\n" % d.getVar('WORKDIR'))
        f.write("urllist = %s\n" % d.getVar("OSCURLLIST"))
        f.write("extra-pkgs = gzip\n")
        f.write("\n")
        f.write("[%s]\n" % ud.host)
        f.write("user = %s\n" % ud.parm["user"])
        f.write("pass = %s\n" % ud.parm["pswd"])
        f.close()

        return config_path
