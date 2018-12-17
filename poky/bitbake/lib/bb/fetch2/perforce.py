# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Fetch' implementation for perforce

"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2016 Kodak Alaris, Inc.
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
import logging
import bb
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import FetchError
from   bb.fetch2 import logger
from   bb.fetch2 import runfetchcmd

class Perforce(FetchMethod):
    """ Class to fetch from perforce repositories """
    def supports(self, ud, d):
        """ Check to see if a given url can be fetched with perforce. """
        return ud.type in ['p4']

    def urldata_init(self, ud, d):
        """
        Initialize perforce specific variables within url data.  If P4CONFIG is
        provided by the env, use it.  If P4PORT is specified by the recipe, use
        its values, which may override the settings in P4CONFIG.
        """
        ud.basecmd = d.getVar("FETCHCMD_p4") or "/usr/bin/env p4"

        ud.dldir = d.getVar("P4DIR") or (d.getVar("DL_DIR") + "/p4")

        path = ud.url.split('://')[1]
        path = path.split(';')[0]
        delim = path.find('@');
        if delim != -1:
            (ud.user, ud.pswd) = path.split('@')[0].split(':')
            ud.path = path.split('@')[1]
        else:
            ud.path = path

        ud.usingp4config = False
        p4port = d.getVar('P4PORT')

        if p4port:
            logger.debug(1, 'Using recipe provided P4PORT: %s' % p4port)
            ud.host = p4port
        else:
            logger.debug(1, 'Trying to use P4CONFIG to automatically set P4PORT...')
            ud.usingp4config = True
            p4cmd = '%s info | grep "Server address"' % ud.basecmd
            bb.fetch2.check_network_access(d, p4cmd, ud.url)
            ud.host = runfetchcmd(p4cmd, d, True)
            ud.host = ud.host.split(': ')[1].strip()
            logger.debug(1, 'Determined P4PORT to be: %s' % ud.host)
            if not ud.host:
                raise FetchError('Could not determine P4PORT from P4CONFIG')
	
        if ud.path.find('/...') >= 0:
            ud.pathisdir = True
        else:
            ud.pathisdir = False

        cleanedpath = ud.path.replace('/...', '').replace('/', '.')
        cleanedhost = ud.host.replace(':', '.')
        ud.pkgdir = os.path.join(ud.dldir, cleanedhost, cleanedpath)

        ud.setup_revisions(d)

        ud.localfile = d.expand('%s_%s_%s.tar.gz' % (cleanedhost, cleanedpath, ud.revision))

    def _buildp4command(self, ud, d, command, depot_filename=None):
        """
        Build a p4 commandline.  Valid commands are "changes", "print", and
        "files".  depot_filename is the full path to the file in the depot
        including the trailing '#rev' value.
        """
        p4opt = ""

        if ud.user:
            p4opt += ' -u "%s"' % (ud.user)

        if ud.pswd:
            p4opt += ' -P "%s"' % (ud.pswd)

        if ud.host and not ud.usingp4config:
            p4opt += ' -p %s' % (ud.host)

        if hasattr(ud, 'revision') and ud.revision:
            pathnrev = '%s@%s' % (ud.path, ud.revision)
        else:
            pathnrev = '%s' % (ud.path)

        if depot_filename:
            if ud.pathisdir: # Remove leading path to obtain filename
                filename = depot_filename[len(ud.path)-1:]
            else:
                filename = depot_filename[depot_filename.rfind('/'):]
            filename = filename[:filename.find('#')] # Remove trailing '#rev'

        if command == 'changes':
            p4cmd = '%s%s changes -m 1 //%s' % (ud.basecmd, p4opt, pathnrev)
        elif command == 'print':
            if depot_filename != None:
                p4cmd = '%s%s print -o "p4/%s" "%s"' % (ud.basecmd, p4opt, filename, depot_filename)
            else:
                raise FetchError('No depot file name provided to p4 %s' % command, ud.url)
        elif command == 'files':
            p4cmd = '%s%s files //%s' % (ud.basecmd, p4opt, pathnrev)
        else:
            raise FetchError('Invalid p4 command %s' % command, ud.url)

        return p4cmd

    def _p4listfiles(self, ud, d):
        """
        Return a list of the file names which are present in the depot using the
        'p4 files' command, including trailing '#rev' file revision indicator
        """
        p4cmd = self._buildp4command(ud, d, 'files')
        bb.fetch2.check_network_access(d, p4cmd, ud.url)
        p4fileslist = runfetchcmd(p4cmd, d, True)
        p4fileslist = [f.rstrip() for f in p4fileslist.splitlines()]

        if not p4fileslist:
            raise FetchError('Unable to fetch listing of p4 files from %s@%s' % (ud.host, ud.path))

        count = 0
        filelist = []

        for filename in p4fileslist:
            item = filename.split(' - ')
            lastaction = item[1].split()
            logger.debug(1, 'File: %s Last Action: %s' % (item[0], lastaction[0]))
            if lastaction[0] == 'delete':
                continue
            filelist.append(item[0])

        return filelist

    def download(self, ud, d):
        """ Get the list of files, fetch each one """
        filelist = self._p4listfiles(ud, d)
        if not filelist:
            raise FetchError('No files found in depot %s@%s' % (ud.host, ud.path))

        bb.utils.remove(ud.pkgdir, True)
        bb.utils.mkdirhier(ud.pkgdir)

        for afile in filelist:
            p4fetchcmd = self._buildp4command(ud, d, 'print', afile)
            bb.fetch2.check_network_access(d, p4fetchcmd, ud.url)
            runfetchcmd(p4fetchcmd, d, workdir=ud.pkgdir)

        runfetchcmd('tar -czf %s p4' % (ud.localpath), d, cleanup=[ud.localpath], workdir=ud.pkgdir)

    def clean(self, ud, d):
        """ Cleanup p4 specific files and dirs"""
        bb.utils.remove(ud.localpath)
        bb.utils.remove(ud.pkgdir, True)

    def supports_srcrev(self):
        return True

    def _revision_key(self, ud, d, name):
        """ Return a unique key for the url """
        return 'p4:%s' % ud.pkgdir

    def _latest_revision(self, ud, d, name):
        """ Return the latest upstream scm revision number """
        p4cmd = self._buildp4command(ud, d, "changes")
        bb.fetch2.check_network_access(d, p4cmd, ud.url)
        tip = runfetchcmd(p4cmd, d, True)

        if not tip:
            raise FetchError('Could not determine the latest perforce changelist')

        tipcset = tip.split(' ')[1]
        logger.debug(1, 'p4 tip found to be changelist %s' % tipcset)
        return tipcset

    def sortable_revision(self, ud, d, name):
        """ Return a sortable revision number """
        return False, self._build_revision(ud, d)

    def _build_revision(self, ud, d):
        return ud.revision

