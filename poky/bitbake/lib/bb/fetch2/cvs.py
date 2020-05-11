"""
BitBake 'Fetch' implementations

Classes for obtaining upstream sources for the
BitBake build tools.

"""

# Copyright (C) 2003, 2004  Chris Larson
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig
#

import os
import bb
from bb.fetch2 import FetchMethod, FetchError, MissingParameterError, logger
from bb.fetch2 import runfetchcmd

class Cvs(FetchMethod):
    """
    Class to fetch a module or modules from cvs repositories
    """
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with cvs.
        """
        return ud.type in ['cvs']

    def urldata_init(self, ud, d):
        if not "module" in ud.parm:
            raise MissingParameterError("module", ud.url)
        ud.module = ud.parm["module"]

        ud.tag = ud.parm.get('tag', "")

        # Override the default date in certain cases
        if 'date' in ud.parm:
            ud.date = ud.parm['date']
        elif ud.tag:
            ud.date = ""

        norecurse = ''
        if 'norecurse' in ud.parm:
            norecurse = '_norecurse'

        fullpath = ''
        if 'fullpath' in ud.parm:
            fullpath = '_fullpath'

        ud.localfile = d.expand('%s_%s_%s_%s%s%s.tar.gz' % (ud.module.replace('/', '.'), ud.host, ud.tag, ud.date, norecurse, fullpath))

    def need_update(self, ud, d):
        if (ud.date == "now"):
            return True
        if not os.path.exists(ud.localpath):
            return True
        return False

    def download(self, ud, d):

        method = ud.parm.get('method', 'pserver')
        localdir = ud.parm.get('localdir', ud.module)
        cvs_port = ud.parm.get('port', '')

        cvs_rsh = None
        if method == "ext":
            if "rsh" in ud.parm:
                cvs_rsh = ud.parm["rsh"]

        if method == "dir":
            cvsroot = ud.path
        else:
            cvsroot = ":" + method
            cvsproxyhost = d.getVar('CVS_PROXY_HOST')
            if cvsproxyhost:
                cvsroot += ";proxy=" + cvsproxyhost
            cvsproxyport = d.getVar('CVS_PROXY_PORT')
            if cvsproxyport:
                cvsroot += ";proxyport=" + cvsproxyport
            cvsroot += ":" + ud.user
            if ud.pswd:
                cvsroot += ":" + ud.pswd
            cvsroot += "@" + ud.host + ":" + cvs_port + ud.path

        options = []
        if 'norecurse' in ud.parm:
            options.append("-l")
        if ud.date:
            # treat YYYYMMDDHHMM specially for CVS
            if len(ud.date) == 12:
                options.append("-D \"%s %s:%s UTC\"" % (ud.date[0:8], ud.date[8:10], ud.date[10:12]))
            else:
                options.append("-D \"%s UTC\"" % ud.date)
        if ud.tag:
            options.append("-r %s" % ud.tag)

        cvsbasecmd = d.getVar("FETCHCMD_cvs") or "/usr/bin/env cvs"
        cvscmd = cvsbasecmd + " '-d" + cvsroot + "' co " + " ".join(options) + " " + ud.module
        cvsupdatecmd = cvsbasecmd + " '-d" + cvsroot + "' update -d -P " + " ".join(options)

        if cvs_rsh:
            cvscmd = "CVS_RSH=\"%s\" %s" % (cvs_rsh, cvscmd)
            cvsupdatecmd = "CVS_RSH=\"%s\" %s" % (cvs_rsh, cvsupdatecmd)

        # create module directory
        logger.debug(2, "Fetch: checking for module directory")
        pkg = d.getVar('PN')
        cvsdir = d.getVar("CVSDIR") or (d.getVar("DL_DIR") + "/cvs")
        pkgdir = os.path.join(cvsdir, pkg)
        moddir = os.path.join(pkgdir, localdir)
        workdir = None
        if os.access(os.path.join(moddir, 'CVS'), os.R_OK):
            logger.info("Update " + ud.url)
            bb.fetch2.check_network_access(d, cvsupdatecmd, ud.url)
            # update sources there
            workdir = moddir
            cmd = cvsupdatecmd
        else:
            logger.info("Fetch " + ud.url)
            # check out sources there
            bb.utils.mkdirhier(pkgdir)
            workdir = pkgdir
            logger.debug(1, "Running %s", cvscmd)
            bb.fetch2.check_network_access(d, cvscmd, ud.url)
            cmd = cvscmd

        runfetchcmd(cmd, d, cleanup=[moddir], workdir=workdir)

        if not os.access(moddir, os.R_OK):
            raise FetchError("Directory %s was not readable despite sucessful fetch?!" % moddir, ud.url)

        scmdata = ud.parm.get("scmdata", "")
        if scmdata == "keep":
            tar_flags = ""
        else:
            tar_flags = "--exclude='CVS'"

        # tar them up to a defined filename
        workdir = None
        if 'fullpath' in ud.parm:
            workdir = pkgdir
            cmd = "tar %s -czf %s %s" % (tar_flags, ud.localpath, localdir)
        else:
            workdir = os.path.dirname(os.path.realpath(moddir))
            cmd = "tar %s -czf %s %s" % (tar_flags, ud.localpath, os.path.basename(moddir))

        runfetchcmd(cmd, d, cleanup=[ud.localpath], workdir=workdir)

    def clean(self, ud, d):
        """ Clean CVS Files and tarballs """

        pkg = d.getVar('PN')
        pkgdir = os.path.join(d.getVar("CVSDIR"), pkg)

        bb.utils.remove(pkgdir, True)
        bb.utils.remove(ud.localpath)

