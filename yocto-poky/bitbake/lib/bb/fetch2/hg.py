# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' implementation for mercurial DRCS (hg).

"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2004        Marcin Juszkiewicz
# Copyright (C) 2007        Robert Schuster
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
import errno
from bb import data
from bb.fetch2 import FetchMethod
from bb.fetch2 import FetchError
from bb.fetch2 import MissingParameterError
from bb.fetch2 import runfetchcmd
from bb.fetch2 import logger

class Hg(FetchMethod):
    """Class to fetch from mercurial repositories"""
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with mercurial.
        """
        return ud.type in ['hg']

    def supports_checksum(self, urldata):
        """
        Don't require checksums for local archives created from
        repository checkouts.
        """ 
        return False

    def urldata_init(self, ud, d):
        """
        init hg specific variable within url data
        """
        if not "module" in ud.parm:
            raise MissingParameterError('module', ud.url)

        ud.module = ud.parm["module"]

        if 'protocol' in ud.parm:
            ud.proto = ud.parm['protocol']
        elif not ud.host:
            ud.proto = 'file'
        else:
            ud.proto = "hg"

        ud.setup_revisons(d)

        if 'rev' in ud.parm:
            ud.revision = ud.parm['rev']
        elif not ud.revision:
            ud.revision = self.latest_revision(ud, d)

        # Create paths to mercurial checkouts
        hgsrcname = '%s_%s_%s' % (ud.module.replace('/', '.'), \
                            ud.host, ud.path.replace('/', '.'))
        ud.mirrortarball = 'hg_%s.tar.gz' % hgsrcname
        ud.fullmirror = os.path.join(d.getVar("DL_DIR", True), ud.mirrortarball)

        hgdir = d.getVar("HGDIR", True) or (d.getVar("DL_DIR", True) + "/hg/")
        ud.pkgdir = os.path.join(hgdir, hgsrcname)
        ud.moddir = os.path.join(ud.pkgdir, ud.module)
        ud.localfile = ud.moddir
        ud.basecmd = data.getVar("FETCHCMD_hg", d, True) or "/usr/bin/env hg"

        ud.write_tarballs = d.getVar("BB_GENERATE_MIRROR_TARBALLS", True)

    def need_update(self, ud, d):
        revTag = ud.parm.get('rev', 'tip')
        if revTag == "tip":
            return True
        if not os.path.exists(ud.localpath):
            return True
        return False

    def try_premirror(self, ud, d):
        # If we don't do this, updating an existing checkout with only premirrors
        # is not possible
        if d.getVar("BB_FETCH_PREMIRRORONLY", True) is not None:
            return True
        if os.path.exists(ud.moddir):
            return False
        return True

    def _buildhgcommand(self, ud, d, command):
        """
        Build up an hg commandline based on ud
        command is "fetch", "update", "info"
        """

        proto = ud.parm.get('protocol', 'http')

        host = ud.host
        if proto == "file":
            host = "/"
            ud.host = "localhost"

        if not ud.user:
            hgroot = host + ud.path
        else:
            if ud.pswd:
                hgroot = ud.user + ":" + ud.pswd + "@" + host + ud.path
            else:
                hgroot = ud.user + "@" + host + ud.path

        if command == "info":
            return "%s identify -i %s://%s/%s" % (ud.basecmd, proto, hgroot, ud.module)

        options = [];

        # Don't specify revision for the fetch; clone the entire repo.
        # This avoids an issue if the specified revision is a tag, because
        # the tag actually exists in the specified revision + 1, so it won't
        # be available when used in any successive commands.
        if ud.revision and command != "fetch":
            options.append("-r %s" % ud.revision)

        if command == "fetch":
            if ud.user and ud.pswd:
                cmd = "%s --config auth.default.prefix=* --config auth.default.username=%s --config auth.default.password=%s --config \"auth.default.schemes=%s\" clone %s %s://%s/%s %s" % (ud.basecmd, ud.user, ud.pswd, proto, " ".join(options), proto, hgroot, ud.module, ud.module)
            else:
                cmd = "%s clone %s %s://%s/%s %s" % (ud.basecmd, " ".join(options), proto, hgroot, ud.module, ud.module)
        elif command == "pull":
            # do not pass options list; limiting pull to rev causes the local
            # repo not to contain it and immediately following "update" command
            # will crash
            if ud.user and ud.pswd:
                cmd = "%s --config auth.default.prefix=* --config auth.default.username=%s --config auth.default.password=%s --config \"auth.default.schemes=%s\" pull" % (ud.basecmd, ud.user, ud.pswd, proto)
            else:
                cmd = "%s pull" % (ud.basecmd)
        elif command == "update":
            if ud.user and ud.pswd:
                cmd = "%s --config auth.default.prefix=* --config auth.default.username=%s --config auth.default.password=%s --config \"auth.default.schemes=%s\" update -C %s" % (ud.basecmd, ud.user, ud.pswd, proto, " ".join(options))
            else:
                cmd = "%s update -C %s" % (ud.basecmd, " ".join(options))
        else:
            raise FetchError("Invalid hg command %s" % command, ud.url)

        return cmd

    def download(self, ud, d):
        """Fetch url"""

        logger.debug(2, "Fetch: checking for module directory '" + ud.moddir + "'")

        # If the checkout doesn't exist and the mirror tarball does, extract it
        if not os.path.exists(ud.pkgdir) and os.path.exists(ud.fullmirror):
            bb.utils.mkdirhier(ud.pkgdir)
            os.chdir(ud.pkgdir)
            runfetchcmd("tar -xzf %s" % (ud.fullmirror), d)

        if os.access(os.path.join(ud.moddir, '.hg'), os.R_OK):
            # Found the source, check whether need pull
            updatecmd = self._buildhgcommand(ud, d, "update")
            os.chdir(ud.moddir)
            logger.debug(1, "Running %s", updatecmd)
            try:
                runfetchcmd(updatecmd, d)
            except bb.fetch2.FetchError:
                # Runnning pull in the repo
                pullcmd = self._buildhgcommand(ud, d, "pull")
                logger.info("Pulling " + ud.url)
                # update sources there
                os.chdir(ud.moddir)
                logger.debug(1, "Running %s", pullcmd)
                bb.fetch2.check_network_access(d, pullcmd, ud.url)
                runfetchcmd(pullcmd, d)
                try:
                    os.unlink(ud.fullmirror)
                except OSError as exc:
                    if exc.errno != errno.ENOENT:
                        raise

        # No source found, clone it.
        if not os.path.exists(ud.moddir):
            fetchcmd = self._buildhgcommand(ud, d, "fetch")
            logger.info("Fetch " + ud.url)
            # check out sources there
            bb.utils.mkdirhier(ud.pkgdir)
            os.chdir(ud.pkgdir)
            logger.debug(1, "Running %s", fetchcmd)
            bb.fetch2.check_network_access(d, fetchcmd, ud.url)
            runfetchcmd(fetchcmd, d)

        # Even when we clone (fetch), we still need to update as hg's clone
        # won't checkout the specified revision if its on a branch
        updatecmd = self._buildhgcommand(ud, d, "update")
        os.chdir(ud.moddir)
        logger.debug(1, "Running %s", updatecmd)
        runfetchcmd(updatecmd, d)

    def clean(self, ud, d):
        """ Clean the hg dir """

        bb.utils.remove(ud.localpath, True)
        bb.utils.remove(ud.fullmirror)
        bb.utils.remove(ud.fullmirror + ".done")

    def supports_srcrev(self):
        return True

    def _latest_revision(self, ud, d, name):
        """
        Compute tip revision for the url
        """
        bb.fetch2.check_network_access(d, self._buildhgcommand(ud, d, "info"))
        output = runfetchcmd(self._buildhgcommand(ud, d, "info"), d)
        return output.strip()

    def _build_revision(self, ud, d, name):
        return ud.revision

    def _revision_key(self, ud, d, name):
        """
        Return a unique key for the url
        """
        return "hg:" + ud.moddir

    def build_mirror_data(self, ud, d):
        # Generate a mirror tarball if needed
        if ud.write_tarballs == "1" and not os.path.exists(ud.fullmirror):
            # it's possible that this symlink points to read-only filesystem with PREMIRROR
            if os.path.islink(ud.fullmirror):
                os.unlink(ud.fullmirror)

            os.chdir(ud.pkgdir)
            logger.info("Creating tarball of hg repository")
            runfetchcmd("tar -czf %s %s" % (ud.fullmirror, ud.module), d)
            runfetchcmd("touch %s.done" % (ud.fullmirror), d)

    def localpath(self, ud, d):
        return ud.pkgdir

    def unpack(self, ud, destdir, d):
        """
        Make a local clone or export for the url
        """

        revflag = "-r %s" % ud.revision
        subdir = ud.parm.get("destsuffix", ud.module)
        codir = "%s/%s" % (destdir, subdir)

        scmdata = ud.parm.get("scmdata", "")
        if scmdata != "nokeep":
            if not os.access(os.path.join(codir, '.hg'), os.R_OK):
                logger.debug(2, "Unpack: creating new hg repository in '" + codir + "'")
                runfetchcmd("%s init %s" % (ud.basecmd, codir), d)
            logger.debug(2, "Unpack: updating source in '" + codir + "'")
            os.chdir(codir)
            runfetchcmd("%s pull %s" % (ud.basecmd, ud.moddir), d)
            runfetchcmd("%s up -C %s" % (ud.basecmd, revflag), d)
        else:
            logger.debug(2, "Unpack: extracting source to '" + codir + "'")
            os.chdir(ud.moddir)
            runfetchcmd("%s archive -t files %s %s" % (ud.basecmd, revflag, codir), d)
