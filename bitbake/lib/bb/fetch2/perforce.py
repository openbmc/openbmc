# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' implementations

Classes for obtaining upstream sources for the
BitBake build tools.

"""

# Copyright (C) 2003, 2004  Chris Larson
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

from future_builtins import zip
import os
import subprocess
import logging
import bb
from   bb import data
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import FetchError
from   bb.fetch2 import logger
from   bb.fetch2 import runfetchcmd

class Perforce(FetchMethod):
    def supports(self, ud, d):
        return ud.type in ['p4']

    def doparse(url, d):
        parm = {}
        path = url.split("://")[1]
        delim = path.find("@");
        if delim != -1:
            (user, pswd, host, port) = path.split('@')[0].split(":")
            path = path.split('@')[1]
        else:
            (host, port) = d.getVar('P4PORT', False).split(':')
            user = ""
            pswd = ""

        if path.find(";") != -1:
            keys=[]
            values=[]
            plist = path.split(';')
            for item in plist:
                if item.count('='):
                    (key, value) = item.split('=')
                    keys.append(key)
                    values.append(value)

            parm = dict(zip(keys, values))
        path = "//" + path.split(';')[0]
        host += ":%s" % (port)
        parm["cset"] = Perforce.getcset(d, path, host, user, pswd, parm)

        return host, path, user, pswd, parm
    doparse = staticmethod(doparse)

    def getcset(d, depot, host, user, pswd, parm):
        p4opt = ""
        if "cset" in parm:
            return parm["cset"];
        if user:
            p4opt += " -u %s" % (user)
        if pswd:
            p4opt += " -P %s" % (pswd)
        if host:
            p4opt += " -p %s" % (host)

        p4date = d.getVar("P4DATE", True)
        if "revision" in parm:
            depot += "#%s" % (parm["revision"])
        elif "label" in parm:
            depot += "@%s" % (parm["label"])
        elif p4date:
            depot += "@%s" % (p4date)

        p4cmd = d.getVar('FETCHCMD_p4', True) or "p4"
        logger.debug(1, "Running %s%s changes -m 1 %s", p4cmd, p4opt, depot)
        p4file, errors = bb.process.run("%s%s changes -m 1 %s" % (p4cmd, p4opt, depot))
        cset = p4file.strip()
        logger.debug(1, "READ %s", cset)
        if not cset:
            return -1

        return cset.split(' ')[1]
    getcset = staticmethod(getcset)

    def urldata_init(self, ud, d):
        (host, path, user, pswd, parm) = Perforce.doparse(ud.url, d)

        base_path = path.replace('/...', '')
        base_path = self._strip_leading_slashes(base_path)
        
        if "label" in parm:
            version = parm["label"]
        else:
            version = Perforce.getcset(d, path, host, user, pswd, parm)

        ud.localfile = data.expand('%s+%s+%s.tar.gz' % (host, base_path.replace('/', '.'), version), d)

    def download(self, ud, d):
        """
        Fetch urls
        """

        (host, depot, user, pswd, parm) = Perforce.doparse(ud.url, d)

        if depot.find('/...') != -1:
            path = depot[:depot.find('/...')]
        else:
            path = depot[:depot.rfind('/')]

        module = parm.get('module', os.path.basename(path))

        # Get the p4 command
        p4opt = ""
        if user:
            p4opt += " -u %s" % (user)

        if pswd:
            p4opt += " -P %s" % (pswd)

        if host:
            p4opt += " -p %s" % (host)

        p4cmd = d.getVar('FETCHCMD_p4', True) or "p4"

        # create temp directory
        logger.debug(2, "Fetch: creating temporary directory")
        bb.utils.mkdirhier(d.expand('${WORKDIR}'))
        mktemp = d.getVar("FETCHCMD_p4mktemp", True) or d.expand("mktemp -d -q '${WORKDIR}/oep4.XXXXXX'")
        tmpfile, errors = bb.process.run(mktemp)
        tmpfile = tmpfile.strip()
        if not tmpfile:
            raise FetchError("Fetch: unable to create temporary directory.. make sure 'mktemp' is in the PATH.", ud.url)

        if "label" in parm:
            depot = "%s@%s" % (depot, parm["label"])
        else:
            cset = Perforce.getcset(d, depot, host, user, pswd, parm)
            depot = "%s@%s" % (depot, cset)

        os.chdir(tmpfile)
        logger.info("Fetch " + ud.url)
        logger.info("%s%s files %s", p4cmd, p4opt, depot)
        p4file, errors = bb.process.run("%s%s files %s" % (p4cmd, p4opt, depot))
        p4file = [f.rstrip() for f in p4file.splitlines()]

        if not p4file:
            raise FetchError("Fetch: unable to get the P4 files from %s" % depot, ud.url)

        count = 0

        for file in p4file:
            list = file.split()

            if list[2] == "delete":
                continue

            dest = list[0][len(path)+1:]
            where = dest.find("#")

            subprocess.call("%s%s print -o %s/%s %s" % (p4cmd, p4opt, module, dest[:where], list[0]), shell=True)
            count = count + 1

        if count == 0:
            logger.error()
            raise FetchError("Fetch: No files gathered from the P4 fetch", ud.url)

        runfetchcmd("tar -czf %s %s" % (ud.localpath, module), d, cleanup = [ud.localpath])
        # cleanup
        bb.utils.prunedir(tmpfile)
