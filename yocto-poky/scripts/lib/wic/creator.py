#!/usr/bin/env python -tt
#
# Copyright (c) 2011 Intel, Inc.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; version 2 of the License
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.

import os, sys
from optparse import OptionParser, SUPPRESS_HELP

from wic import msger
from wic.utils import errors
from wic.conf import configmgr
from wic.plugin import pluginmgr


class Creator(object):
    """${name}: create an image

    Usage:
        ${name} SUBCOMMAND <ksfile> [OPTS]

    ${command_list}
    ${option_list}
    """

    name = 'wic create(cr)'

    def __init__(self, *args, **kwargs):
        self._subcmds = {}

        # get cmds from pluginmgr
        # mix-in do_subcmd interface
        for subcmd, klass in pluginmgr.get_plugins('imager').iteritems():
            if not hasattr(klass, 'do_create'):
                msger.warning("Unsupported subcmd: %s" % subcmd)
                continue

            func = getattr(klass, 'do_create')
            self._subcmds[subcmd] = func

    def get_optparser(self):
        optparser = OptionParser()
        optparser.add_option('-d', '--debug', action='store_true',
                             dest='debug',
                             help=SUPPRESS_HELP)
        optparser.add_option('-v', '--verbose', action='store_true',
                             dest='verbose',
                             help=SUPPRESS_HELP)
        optparser.add_option('', '--logfile', type='string', dest='logfile',
                             default=None,
                             help='Path of logfile')
        optparser.add_option('-c', '--config', type='string', dest='config',
                             default=None,
                             help='Specify config file for wic')
        optparser.add_option('-o', '--outdir', type='string', action='store',
                             dest='outdir', default=None,
                             help='Output directory')
        optparser.add_option('', '--tmpfs', action='store_true', dest='enabletmpfs',
                             help='Setup tmpdir as tmpfs to accelerate, experimental'
                                  ' feature, use it if you have more than 4G memory')
        return optparser

    def postoptparse(self, options):
        abspath = lambda pth: os.path.abspath(os.path.expanduser(pth))

        if options.verbose:
            msger.set_loglevel('verbose')
        if options.debug:
            msger.set_loglevel('debug')

        if options.logfile:
            logfile_abs_path = abspath(options.logfile)
            if os.path.isdir(logfile_abs_path):
                raise errors.Usage("logfile's path %s should be file"
                                   % options.logfile)
            if not os.path.exists(os.path.dirname(logfile_abs_path)):
                os.makedirs(os.path.dirname(logfile_abs_path))
            msger.set_interactive(False)
            msger.set_logfile(logfile_abs_path)
            configmgr.create['logfile'] = options.logfile

        if options.config:
            configmgr.reset()
            configmgr._siteconf = options.config

        if options.outdir is not None:
            configmgr.create['outdir'] = abspath(options.outdir)

        cdir = 'outdir'
        if os.path.exists(configmgr.create[cdir]) \
           and not os.path.isdir(configmgr.create[cdir]):
            msger.error('Invalid directory specified: %s' \
                        % configmgr.create[cdir])

        if options.enabletmpfs:
            configmgr.create['enabletmpfs'] = options.enabletmpfs

    def main(self, argv=None):
        if argv is None:
            argv = sys.argv
        else:
            argv = argv[:] # don't modify caller's list

        pname = argv[0]
        if pname not in self._subcmds:
            msger.error('Unknown plugin: %s' % pname)

        optparser = self.get_optparser()
        options, args = optparser.parse_args(argv)

        self.postoptparse(options)

        return self._subcmds[pname](options, *args[1:])
