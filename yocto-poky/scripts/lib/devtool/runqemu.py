# Development tool - runqemu command plugin
#
# Copyright (C) 2015 Intel Corporation
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

"""Devtool runqemu plugin"""

import os
import bb
import logging
import argparse
import glob
from devtool import exec_build_env_command, setup_tinfoil, DevtoolError

logger = logging.getLogger('devtool')

def runqemu(args, config, basepath, workspace):
    """Entry point for the devtool 'runqemu' subcommand"""

    tinfoil = setup_tinfoil(config_only=True, basepath=basepath)
    machine = tinfoil.config_data.getVar('MACHINE', True)
    bindir_native = tinfoil.config_data.getVar('STAGING_BINDIR_NATIVE', True)
    tinfoil.shutdown()

    if not glob.glob(os.path.join(bindir_native, 'qemu-system-*')):
        raise DevtoolError('QEMU is not available within this SDK')

    imagename = args.imagename
    if not imagename:
        sdk_targets = config.get('SDK', 'sdk_targets', '').split()
        if sdk_targets:
            imagename = sdk_targets[0]
    if not imagename:
        raise DevtoolError('Unable to determine image name to run, please specify one')

    try:
        exec_build_env_command(config.init_path, basepath, 'runqemu %s %s %s' % (machine, imagename, " ".join(args.args)), watch=True)
    except bb.process.ExecutionError as e:
        # We've already seen the output since watch=True, so just ensure we return something to the user
        return e.exitcode

    return 0

def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""
    if context.fixed_setup:
        parser_runqemu = subparsers.add_parser('runqemu', help='Run QEMU on the specified image',
                                               description='Runs QEMU to boot the specified image',
                                               group='testbuild', order=-20)
        parser_runqemu.add_argument('imagename', help='Name of built image to boot within QEMU', nargs='?')
        parser_runqemu.add_argument('args', help='Any remaining arguments are passed to the runqemu script (pass --help after imagename to see what these are)',
                                    nargs=argparse.REMAINDER)
        parser_runqemu.set_defaults(func=runqemu)
