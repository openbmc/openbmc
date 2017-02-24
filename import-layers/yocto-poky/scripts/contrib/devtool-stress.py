#!/usr/bin/env python3

# devtool stress tester
#
# Written by: Paul Eggleton <paul.eggleton@linux.intel.com>
#
# Copyright 2015 Intel Corporation
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

import sys
import os
import os.path
import subprocess
import re
import argparse
import logging
import tempfile
import shutil
import signal
import fnmatch

scripts_lib_path = os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), '..', 'lib'))
sys.path.insert(0, scripts_lib_path)
import scriptutils
import argparse_oe
logger = scriptutils.logger_create('devtool-stress')

def select_recipes(args):
    import bb.tinfoil
    tinfoil = bb.tinfoil.Tinfoil()
    tinfoil.prepare(False)

    pkg_pn = tinfoil.cooker.recipecaches[''].pkg_pn
    (latest_versions, preferred_versions) = bb.providers.findProviders(tinfoil.config_data, tinfoil.cooker.recipecaches[''], pkg_pn)

    skip_classes = args.skip_classes.split(',')

    recipelist = []
    for pn in sorted(pkg_pn):
        pref = preferred_versions[pn]
        inherits = [os.path.splitext(os.path.basename(f))[0] for f in tinfoil.cooker.recipecaches[''].inherits[pref[1]]]
        for cls in skip_classes:
            if cls in inherits:
                break
        else:
            recipelist.append(pn)

    tinfoil.shutdown()

    resume_from = args.resume_from
    if resume_from:
        if not resume_from in recipelist:
            print('%s is not a testable recipe' % resume_from)
            return 1
    if args.only:
        only = args.only.split(',')
        for onlyitem in only:
            for pn in recipelist:
                if fnmatch.fnmatch(pn, onlyitem):
                    break
            else:
                print('%s does not match any testable recipe' % onlyitem)
                return 1
    else:
        only = None
    if args.skip:
        skip = args.skip.split(',')
    else:
        skip = []

    recipes = []
    for pn in recipelist:
        if resume_from:
            if pn == resume_from:
                resume_from = None
            else:
                continue

        if args.only:
            for item in only:
                if fnmatch.fnmatch(pn, item):
                    break
            else:
                continue

        skipit = False
        for item in skip:
            if fnmatch.fnmatch(pn, item):
                skipit = True
        if skipit:
            continue

        recipes.append(pn)

    return recipes


def stress_extract(args):
    import bb.process

    recipes = select_recipes(args)

    failures = 0
    tmpdir = tempfile.mkdtemp()
    os.setpgrp()
    try:
        for pn in recipes:
            sys.stdout.write('Testing %s ' % (pn + ' ').ljust(40, '.'))
            sys.stdout.flush()
            failed = False
            skipped = None

            srctree = os.path.join(tmpdir, pn)
            try:
                bb.process.run('devtool extract %s %s' % (pn, srctree))
            except bb.process.ExecutionError as exc:
                if exc.exitcode == 4:
                    skipped = 'incompatible'
                else:
                    failed = True
                    with open('stress_%s_extract.log' % pn, 'w') as f:
                        f.write(str(exc))

            if os.path.exists(srctree):
                shutil.rmtree(srctree)

            if failed:
                print('failed')
                failures += 1
            elif skipped:
                print('skipped (%s)' % skipped)
            else:
                print('ok')
    except KeyboardInterrupt:
        # We want any child processes killed. This is crude, but effective.
        os.killpg(0, signal.SIGTERM)

    if failures:
        return 1
    else:
        return 0


def stress_modify(args):
    import bb.process

    recipes = select_recipes(args)

    failures = 0
    tmpdir = tempfile.mkdtemp()
    os.setpgrp()
    try:
        for pn in recipes:
            sys.stdout.write('Testing %s ' % (pn + ' ').ljust(40, '.'))
            sys.stdout.flush()
            failed = False
            reset = True
            skipped = None

            srctree = os.path.join(tmpdir, pn)
            try:
                bb.process.run('devtool modify -x %s %s' % (pn, srctree))
            except bb.process.ExecutionError as exc:
                if exc.exitcode == 4:
                    skipped = 'incompatible'
                else:
                    with open('stress_%s_modify.log' % pn, 'w') as f:
                        f.write(str(exc))
                    failed = 'modify'
                    reset = False

            if not skipped:
                if not failed:
                    try:
                        bb.process.run('bitbake -c install %s' % pn)
                    except bb.process.CmdError as exc:
                        with open('stress_%s_install.log' % pn, 'w') as f:
                            f.write(str(exc))
                        failed = 'build'
                if reset:
                    try:
                        bb.process.run('devtool reset %s' % pn)
                    except bb.process.CmdError as exc:
                        print('devtool reset failed: %s' % str(exc))
                        break

            if os.path.exists(srctree):
                shutil.rmtree(srctree)

            if failed:
                print('failed (%s)' % failed)
                failures += 1
            elif skipped:
                print('skipped (%s)' % skipped)
            else:
                print('ok')
    except KeyboardInterrupt:
        # We want any child processes killed. This is crude, but effective.
        os.killpg(0, signal.SIGTERM)

    if failures:
        return 1
    else:
        return 0


def main():
    parser = argparse_oe.ArgumentParser(description="devtool stress tester",
                                        epilog="Use %(prog)s <subcommand> --help to get help on a specific command")
    parser.add_argument('-d', '--debug', help='Enable debug output', action='store_true')
    parser.add_argument('-r', '--resume-from', help='Resume from specified recipe', metavar='PN')
    parser.add_argument('-o', '--only', help='Only test specified recipes (comma-separated without spaces, wildcards allowed)', metavar='PNLIST')
    parser.add_argument('-s', '--skip', help='Skip specified recipes (comma-separated without spaces, wildcards allowed)', metavar='PNLIST', default='gcc-source-*,kernel-devsrc,package-index,perf,meta-world-pkgdata,glibc-locale,glibc-mtrace,glibc-scripts,os-release')
    parser.add_argument('-c', '--skip-classes', help='Skip recipes inheriting specified classes (comma-separated) - default %(default)s', metavar='CLASSLIST', default='native,nativesdk,cross,cross-canadian,image,populate_sdk,meta,packagegroup')
    subparsers = parser.add_subparsers(title='subcommands', metavar='<subcommand>')
    subparsers.required = True

    parser_modify = subparsers.add_parser('modify',
                                          help='Run "devtool modify" followed by a build with bitbake on matching recipes',
                                          description='Runs "devtool modify" followed by a build with bitbake on matching recipes')
    parser_modify.set_defaults(func=stress_modify)

    parser_extract = subparsers.add_parser('extract',
                                           help='Run "devtool extract" on matching recipes',
                                           description='Runs "devtool extract" on matching recipes')
    parser_extract.set_defaults(func=stress_extract)

    args = parser.parse_args()

    if args.debug:
        logger.setLevel(logging.DEBUG)

    import scriptpath
    bitbakepath = scriptpath.add_bitbake_lib_path()
    if not bitbakepath:
        logger.error("Unable to find bitbake by searching parent directory of this script or PATH")
        return 1
    logger.debug('Found bitbake path: %s' % bitbakepath)

    ret = args.func(args)

if __name__ == "__main__":
    main()
