# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
Bitbake "Fetch" implementation for osc (Opensuse build service client).
Based on the svn "Fetch" implementation.

"""

import  os
import  sys
import logging
import  bb
from    bb       import data
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
        relpath = self._strip_leading_slashes(ud.path)
        ud.pkgdir = os.path.join(data.expand('${OSCDIR}', d), ud.host)
        ud.moddir = os.path.join(ud.pkgdir, relpath, ud.module)

        if 'rev' in ud.parm:
            ud.revision = ud.parm['rev']
        else:
            pv = data.getVar("PV", d, 0)
            rev = bb.fetch2.srcrev_internal_helper(ud, d)
            if rev and rev != True:
                ud.revision = rev
            else:
                ud.revision = ""

        ud.localfile = data.expand('%s_%s_%s.tar.gz' % (ud.module.replace('/', '.'), ud.path.replace('/', '.'), ud.revision), d)

    def _buildosccommand(self, ud, d, command):
        """
        Build up an ocs commandline based on ud
        command is "fetch", "update", "info"
        """

        basecmd = data.expand('${FETCHCMD_osc}', d)

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

        if os.access(os.path.join(data.expand('${OSCDIR}', d), ud.path, ud.module), os.R_OK):
            oscupdatecmd = self._buildosccommand(ud, d, "update")
            logger.info("Update "+ ud.url)
            # update sources there
            os.chdir(ud.moddir)
            logger.debug(1, "Running %s", oscupdatecmd)
            bb.fetch2.check_network_access(d, oscupdatecmd, ud.url)
            runfetchcmd(oscupdatecmd, d)
        else:
            oscfetchcmd = self._buildosccommand(ud, d, "fetch")
            logger.info("Fetch " + ud.url)
            # check out sources there
            bb.utils.mkdirhier(ud.pkgdir)
            os.chdir(ud.pkgdir)
            logger.debug(1, "Running %s", oscfetchcmd)
            bb.fetch2.check_network_access(d, oscfetchcmd, ud.url)
            runfetchcmd(oscfetchcmd, d)

        os.chdir(os.path.join(ud.pkgdir + ud.path))
        # tar them up to a defined filename
        runfetchcmd("tar -czf %s %s" % (ud.localpath, ud.module), d, cleanup = [ud.localpath])

    def supports_srcrev(self):
        return False

    def generate_config(self, ud, d):
        """
        Generate a .oscrc to be used for this run.
        """

        config_path = os.path.join(data.expand('${OSCDIR}', d), "oscrc")
        if (os.path.exists(config_path)):
            os.remove(config_path)

        f = open(config_path, 'w')
        f.write("[general]\n")
        f.write("apisrv = %s\n" % ud.host)
        f.write("scheme = http\n")
        f.write("su-wrapper = su -c\n")
        f.write("build-root = %s\n" % data.expand('${WORKDIR}', d))
        f.write("urllist = http://moblin-obs.jf.intel.com:8888/build/%(project)s/%(repository)s/%(buildarch)s/:full/%(name)s.rpm\n")
        f.write("extra-pkgs = gzip\n")
        f.write("\n")
        f.write("[%s]\n" % ud.host)
        f.write("user = %s\n" % ud.parm["user"])
        f.write("pass = %s\n" % ud.parm["pswd"])
        f.close()

        return config_path
