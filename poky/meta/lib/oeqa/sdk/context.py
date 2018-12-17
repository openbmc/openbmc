# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import os
import sys
import glob
import re

from oeqa.core.context import OETestContext, OETestContextExecutor

class OESDKTestContext(OETestContext):
    sdk_files_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "files")

    def __init__(self, td=None, logger=None, sdk_dir=None, sdk_env=None,
            target_pkg_manifest=None, host_pkg_manifest=None):
        super(OESDKTestContext, self).__init__(td, logger)

        self.sdk_dir = sdk_dir
        self.sdk_env = sdk_env
        self.target_pkg_manifest = target_pkg_manifest
        self.host_pkg_manifest = host_pkg_manifest

    def _hasPackage(self, manifest, pkg, regex=False):
        if regex:
            # do regex match
            pat = re.compile(pkg)
            for p in manifest.keys():
                if pat.search(p):
                    return True
        else:
            # do exact match
            if pkg in manifest.keys():
                return True
        return False

    def hasHostPackage(self, pkg, regex=False):
        return self._hasPackage(self.host_pkg_manifest, pkg, regex=regex)

    def hasTargetPackage(self, pkg, multilib=False, regex=False):
        if multilib:
            # match multilib according to sdk_env
            mls = self.td.get('MULTILIB_VARIANTS', '').split()
            for ml in mls:
                if ('ml'+ml) in self.sdk_env:
                    pkg = ml + '-' + pkg
        return self._hasPackage(self.target_pkg_manifest, pkg, regex=regex)

class OESDKTestContextExecutor(OETestContextExecutor):
    _context_class = OESDKTestContext

    name = 'sdk'
    help = 'sdk test component'
    description = 'executes sdk tests'

    default_cases = [os.path.join(os.path.abspath(os.path.dirname(__file__)),
            'cases')]
    default_test_data = None

    def register_commands(self, logger, subparsers):
        super(OESDKTestContextExecutor, self).register_commands(logger, subparsers)

        sdk_group = self.parser.add_argument_group('sdk options')
        sdk_group.add_argument('--sdk-env', action='store',
            help='sdk environment')
        sdk_group.add_argument('--target-manifest', action='store',
            help='sdk target manifest')
        sdk_group.add_argument('--host-manifest', action='store',
            help='sdk host manifest')

        sdk_dgroup = self.parser.add_argument_group('sdk display options')
        sdk_dgroup.add_argument('--list-sdk-env', action='store_true',
            default=False, help='sdk list available environment')

        # XXX this option is required but argparse_oe has a bug handling
        # required options, seems that don't keep track of already parsed
        # options
        sdk_rgroup = self.parser.add_argument_group('sdk required options')
        sdk_rgroup.add_argument('--sdk-dir', required=False, action='store', 
            help='sdk installed directory')

        self.parser.add_argument('-j', '--num-processes', dest='processes', action='store',
                type=int, help="number of processes to execute in parallel with")

    @staticmethod
    def _load_manifest(manifest):
        pkg_manifest = {}
        if manifest:
            with open(manifest) as f:
                for line in f:
                    (pkg, arch, version) = line.strip().split()
                    pkg_manifest[pkg] = (version, arch)

        return pkg_manifest

    def _process_args(self, logger, args):
        super(OESDKTestContextExecutor, self)._process_args(logger, args)

        self.tc_kwargs['init']['sdk_dir'] = args.sdk_dir
        self.tc_kwargs['init']['sdk_env'] = self.sdk_env
        self.tc_kwargs['init']['target_pkg_manifest'] = \
                OESDKTestContextExecutor._load_manifest(args.target_manifest)
        self.tc_kwargs['init']['host_pkg_manifest'] = \
                OESDKTestContextExecutor._load_manifest(args.host_manifest)
        self.tc_kwargs['run']['processes'] = args.processes

    @staticmethod
    def _get_sdk_environs(sdk_dir):
        sdk_env = {}

        environ_pattern = sdk_dir + '/environment-setup-*'
        full_sdk_env = glob.glob(sdk_dir + '/environment-setup-*')
        for env in full_sdk_env:
            m = re.search('environment-setup-(.*)', env)
            if m:
                sdk_env[m.group(1)] = env

        return sdk_env

    def _display_sdk_envs(self, log, args, sdk_envs):
        log("Available SDK environments at directory %s:" \
                % args.sdk_dir)
        log("")
        for env in sdk_envs:
            log(env)

    def run(self, logger, args):
        import argparse_oe

        if not args.sdk_dir:
            raise argparse_oe.ArgumentUsageError("No SDK directory "\
                   "specified please do, --sdk-dir SDK_DIR", self.name)

        sdk_envs = OESDKTestContextExecutor._get_sdk_environs(args.sdk_dir)
        if not sdk_envs:
            raise argparse_oe.ArgumentUsageError("No available SDK "\
                   "enviroments found at %s" % args.sdk_dir, self.name)

        if args.list_sdk_env:
            self._display_sdk_envs(logger.info, args, sdk_envs)
            sys.exit(0)

        if not args.sdk_env in sdk_envs:
            self._display_sdk_envs(logger.error, args, sdk_envs)
            raise argparse_oe.ArgumentUsageError("No valid SDK "\
                   "environment (%s) specified" % args.sdk_env, self.name)

        self.sdk_env = sdk_envs[args.sdk_env]
        return super(OESDKTestContextExecutor, self).run(logger, args)

_executor_class = OESDKTestContextExecutor
