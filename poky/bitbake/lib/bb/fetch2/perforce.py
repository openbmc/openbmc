"""
BitBake 'Fetch' implementation for perforce

Supported SRC_URI options are:

- module
   The top-level location to fetch while preserving the remote paths

   The value of module can point to either a directory or a file. The result,
   in both cases, is that the fetcher will preserve all file paths starting
   from the module path. That is, the top-level directory in the module value
   will also be the top-level directory in P4DIR.

- remotepath
   If the value "keep" is given, the full depot location of each file is
   preserved in P4DIR. This option overrides the effect of the module option.

"""

# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2016 Kodak Alaris, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import bb
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import FetchError
from   bb.fetch2 import logger
from   bb.fetch2 import runfetchcmd

class PerforceProgressHandler (bb.progress.BasicProgressHandler):
    """
    Implements basic progress information for perforce, based on the number of
    files to be downloaded.

    The p4 print command will print one line per file, therefore it can be used
    to "count" the number of files already completed and give an indication of
    the progress.
    """
    def __init__(self, d, num_files):
        self._num_files = num_files
        self._count = 0
        super(PerforceProgressHandler, self).__init__(d)

        # Send an initial progress event so the bar gets shown
        self._fire_progress(-1)

    def write(self, string):
        self._count = self._count + 1

        percent = int(100.0 * float(self._count) / float(self._num_files))

        # In case something goes wrong, we try to preserve our sanity
        if percent > 100:
            percent = 100

        self.update(percent)

        super(PerforceProgressHandler, self).write(string)

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
            logger.debug('Using recipe provided P4PORT: %s' % p4port)
            ud.host = p4port
        else:
            logger.debug('Trying to use P4CONFIG to automatically set P4PORT...')
            ud.usingp4config = True
            p4cmd = '%s info | grep "Server address"' % ud.basecmd
            bb.fetch2.check_network_access(d, p4cmd, ud.url)
            ud.host = runfetchcmd(p4cmd, d, True)
            ud.host = ud.host.split(': ')[1].strip()
            logger.debug('Determined P4PORT to be: %s' % ud.host)
            if not ud.host:
                raise FetchError('Could not determine P4PORT from P4CONFIG')

        # Fetcher options
        ud.module = ud.parm.get('module')
        ud.keepremotepath = (ud.parm.get('remotepath', '') == 'keep')

        if ud.path.find('/...') >= 0:
            ud.pathisdir = True
        else:
            ud.pathisdir = False

        # Avoid using the "/..." syntax in SRC_URI when a module value is given
        if ud.pathisdir and ud.module:
            raise FetchError('SRC_URI depot path cannot not end in /... when a module value is given')

        cleanedpath = ud.path.replace('/...', '').replace('/', '.')
        cleanedhost = ud.host.replace(':', '.')

        cleanedmodule = ""
        # Merge the path and module into the final depot location
        if ud.module:
            if ud.module.find('/') == 0:
                raise FetchError('module cannot begin with /')
            ud.path = os.path.join(ud.path, ud.module)

            # Append the module path to the local pkg name
            cleanedmodule = ud.module.replace('/...', '').replace('/', '.')
            cleanedpath += '--%s' % cleanedmodule

        ud.pkgdir = os.path.join(ud.dldir, cleanedhost, cleanedpath)

        ud.setup_revisions(d)

        ud.localfile = d.expand('%s_%s_%s_%s.tar.gz' % (cleanedhost, cleanedpath, cleanedmodule, ud.revision))

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
            if ud.keepremotepath:
                # preserve everything, remove the leading //
                filename = depot_filename.lstrip('/')
            elif ud.module:
                # remove everything up to the module path
                modulepath = ud.module.rstrip('/...')
                filename = depot_filename[depot_filename.rfind(modulepath):]
            elif ud.pathisdir:
                # Remove leading (visible) path to obtain the filepath
                filename = depot_filename[len(ud.path)-1:]
            else:
                # Remove everything, except the filename
                filename = depot_filename[depot_filename.rfind('/'):]

            filename = filename[:filename.find('#')] # Remove trailing '#rev'

        if command == 'changes':
            p4cmd = '%s%s changes -m 1 //%s' % (ud.basecmd, p4opt, pathnrev)
        elif command == 'print':
            if depot_filename is not None:
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
            logger.debug('File: %s Last Action: %s' % (item[0], lastaction[0]))
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

        progresshandler = PerforceProgressHandler(d, len(filelist))

        for afile in filelist:
            p4fetchcmd = self._buildp4command(ud, d, 'print', afile)
            bb.fetch2.check_network_access(d, p4fetchcmd, ud.url)
            runfetchcmd(p4fetchcmd, d, workdir=ud.pkgdir, log=progresshandler)

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
        logger.debug('p4 tip found to be changelist %s' % tipcset)
        return tipcset

    def sortable_revision(self, ud, d, name):
        """ Return a sortable revision number """
        return False, self._build_revision(ud, d)

    def _build_revision(self, ud, d):
        return ud.revision

