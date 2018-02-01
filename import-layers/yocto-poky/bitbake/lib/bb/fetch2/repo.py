# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake "Fetch" repo (git) implementation

"""

# Copyright (C) 2009 Tom Rini <trini@embeddedalley.com>
#
# Based on git.py which is:
#Copyright (C) 2005 Richard Purdie
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
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import runfetchcmd

class Repo(FetchMethod):
    """Class to fetch a module or modules from repo (git) repositories"""
    def supports(self, ud, d):
        """
        Check to see if a given url can be fetched with repo.
        """
        return ud.type in ["repo"]

    def urldata_init(self, ud, d):
        """
        We don"t care about the git rev of the manifests repository, but
        we do care about the manifest to use.  The default is "default".
        We also care about the branch or tag to be used.  The default is
        "master".
        """

        ud.proto = ud.parm.get('protocol', 'git')
        ud.branch = ud.parm.get('branch', 'master')
        ud.manifest = ud.parm.get('manifest', 'default.xml')
        if not ud.manifest.endswith('.xml'):
            ud.manifest += '.xml'

        ud.localfile = d.expand("repo_%s%s_%s_%s.tar.gz" % (ud.host, ud.path.replace("/", "."), ud.manifest, ud.branch))

    def download(self, ud, d):
        """Fetch url"""

        if os.access(os.path.join(d.getVar("DL_DIR"), ud.localfile), os.R_OK):
            logger.debug(1, "%s already exists (or was stashed). Skipping repo init / sync.", ud.localpath)
            return

        gitsrcname = "%s%s" % (ud.host, ud.path.replace("/", "."))
        repodir = d.getVar("REPODIR") or os.path.join(d.getVar("DL_DIR"), "repo")
        codir = os.path.join(repodir, gitsrcname, ud.manifest)

        if ud.user:
            username = ud.user + "@"
        else:
            username = ""

        repodir = os.path.join(codir, "repo")
        bb.utils.mkdirhier(repodir)
        if not os.path.exists(os.path.join(repodir, ".repo")):
            bb.fetch2.check_network_access(d, "repo init -m %s -b %s -u %s://%s%s%s" % (ud.manifest, ud.branch, ud.proto, username, ud.host, ud.path), ud.url)
            runfetchcmd("repo init -m %s -b %s -u %s://%s%s%s" % (ud.manifest, ud.branch, ud.proto, username, ud.host, ud.path), d, workdir=repodir)

        bb.fetch2.check_network_access(d, "repo sync %s" % ud.url, ud.url)
        runfetchcmd("repo sync", d, workdir=repodir)

        scmdata = ud.parm.get("scmdata", "")
        if scmdata == "keep":
            tar_flags = ""
        else:
            tar_flags = "--exclude='.repo' --exclude='.git'"

        # Create a cache
        runfetchcmd("tar %s -czf %s %s" % (tar_flags, ud.localpath, os.path.join(".", "*") ), d, workdir=codir)

    def supports_srcrev(self):
        return False

    def _build_revision(self, ud, d):
        return ud.manifest

    def _want_sortable_revision(self, ud, d):
        return False
