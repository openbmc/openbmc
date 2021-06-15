#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.core.context import OETestContext, OETestContextExecutor
from oeqa.core.target.ssh import OESSHTarget
from oeqa.core.target.qemu import OEQemuTarget
from oeqa.utils.dump import HostDumper

from oeqa.runtime.loader import OERuntimeTestLoader

class OERuntimeTestContext(OETestContext):
    loaderClass = OERuntimeTestLoader
    runtime_files_dir = os.path.join(
                        os.path.dirname(os.path.abspath(__file__)), "files")

    def __init__(self, td, logger, target,
                 host_dumper, image_packages, extract_dir):
        super(OERuntimeTestContext, self).__init__(td, logger)

        self.target = target
        self.image_packages = image_packages
        self.host_dumper = host_dumper
        self.extract_dir = extract_dir
        self._set_target_cmds()

    def _set_target_cmds(self):
        self.target_cmds = {}

        self.target_cmds['ps'] = 'ps'
        if 'procps' in self.image_packages:
            self.target_cmds['ps'] = self.target_cmds['ps'] + ' -ef'

class OERuntimeTestContextExecutor(OETestContextExecutor):
    _context_class = OERuntimeTestContext

    name = 'runtime'
    help = 'runtime test component'
    description = 'executes runtime tests over targets'

    default_cases = os.path.join(os.path.abspath(os.path.dirname(__file__)),
            'cases')
    default_data = None
    default_test_data = 'data/testdata.json'
    default_tests = ''
    default_json_result_dir = '%s-results' % name

    default_target_type = 'simpleremote'
    default_manifest = 'data/manifest'
    default_server_ip = '192.168.7.1'
    default_target_ip = '192.168.7.2'
    default_extract_dir = 'packages/extracted'

    def register_commands(self, logger, subparsers):
        super(OERuntimeTestContextExecutor, self).register_commands(logger, subparsers)

        runtime_group = self.parser.add_argument_group('runtime options')

        runtime_group.add_argument('--target-type', action='store',
                default=self.default_target_type, choices=['simpleremote', 'qemu'],
                help="Target type of device under test, default: %s" \
                % self.default_target_type)
        runtime_group.add_argument('--target-ip', action='store',
                default=self.default_target_ip,
                help="IP address of device under test, default: %s" \
                % self.default_target_ip)
        runtime_group.add_argument('--server-ip', action='store',
                default=self.default_target_ip,
                help="IP address of device under test, default: %s" \
                % self.default_server_ip)

        runtime_group.add_argument('--host-dumper-dir', action='store',
                help="Directory where host status is dumped, if tests fails")

        runtime_group.add_argument('--packages-manifest', action='store',
                default=self.default_manifest,
                help="Package manifest of the image under test, default: %s" \
                % self.default_manifest)

        runtime_group.add_argument('--extract-dir', action='store',
                default=self.default_extract_dir,
                help='Directory where extracted packages reside, default: %s' \
                % self.default_extract_dir)

        runtime_group.add_argument('--qemu-boot', action='store',
                help="Qemu boot configuration, only needed when target_type is QEMU.")

    @staticmethod
    def getTarget(target_type, logger, target_ip, server_ip, **kwargs):
        target = None

        if target_ip:
            target_ip_port = target_ip.split(':')
            if len(target_ip_port) == 2:
                target_ip = target_ip_port[0]
                kwargs['port'] = target_ip_port[1]

        if server_ip:
            server_ip_port = server_ip.split(':')
            if len(server_ip_port) == 2:
                server_ip = server_ip_port[0]
                kwargs['server_port'] = int(server_ip_port[1])

        if target_type == 'simpleremote':
            target = OESSHTarget(logger, target_ip, server_ip, **kwargs)
        elif target_type == 'qemu':
            target = OEQemuTarget(logger, server_ip, **kwargs)
        else:
            # XXX: This code uses the old naming convention for controllers and
            # targets, the idea it is to leave just targets as the controller
            # most of the time was just a wrapper.
            # XXX: This code tries to import modules from lib/oeqa/controllers
            # directory and treat them as controllers, it will less error prone
            # to use introspection to load such modules.
            # XXX: Don't base your targets on this code it will be refactored
            # in the near future.
            # Custom target module loading
            target_modules_path = kwargs.get('target_modules_path', '')
            controller = OERuntimeTestContextExecutor.getControllerModule(target_type, target_modules_path)
            target = controller(logger, target_ip, server_ip, **kwargs)

        return target

    # Search oeqa.controllers module directory for and return a controller
    # corresponding to the given target name.
    # AttributeError raised if not found.
    # ImportError raised if a provided module can not be imported.
    @staticmethod
    def getControllerModule(target, target_modules_path):
        controllerslist = OERuntimeTestContextExecutor._getControllerModulenames(target_modules_path)
        controller = OERuntimeTestContextExecutor._loadControllerFromName(target, controllerslist)
        return controller

    # Return a list of all python modules in lib/oeqa/controllers for each
    # layer in bbpath
    @staticmethod
    def _getControllerModulenames(target_modules_path):

        controllerslist = []

        def add_controller_list(path):
            if not os.path.exists(os.path.join(path, '__init__.py')):
                raise OSError('Controllers directory %s exists but is missing __init__.py' % path)
            files = sorted([f for f in os.listdir(path) if f.endswith('.py') and not f.startswith('_') and not f.startswith('.#')])
            for f in files:
                module = 'oeqa.controllers.' + f[:-3]
                if module not in controllerslist:
                    controllerslist.append(module)
                else:
                    raise RuntimeError("Duplicate controller module found for %s. Layers should create unique controller module names" % module)

        extpath = target_modules_path.split(':')
        for p in extpath:
            controllerpath = os.path.join(p, 'lib', 'oeqa', 'controllers')
            if os.path.exists(controllerpath):
                add_controller_list(controllerpath)
        return controllerslist

    # Search for and return a controller from given target name and
    # set of module names.
    # Raise AttributeError if not found.
    # Raise ImportError if a provided module can not be imported
    @staticmethod
    def _loadControllerFromName(target, modulenames):
        for name in modulenames:
            obj = OERuntimeTestContextExecutor._loadControllerFromModule(target, name)
            if obj:
                return obj
        raise AttributeError("Unable to load {0} from available modules: {1}".format(target, str(modulenames)))

    # Search for and return a controller or None from given module name
    @staticmethod
    def _loadControllerFromModule(target, modulename):
        obj = None
        # import module, allowing it to raise import exception
        module = __import__(modulename, globals(), locals(), [target])
        # look for target class in the module, catching any exceptions as it
        # is valid that a module may not have the target class.
        try:
            obj = getattr(module, target)
        except:
            obj = None
        return obj

    @staticmethod
    def readPackagesManifest(manifest):
        if not manifest or not os.path.exists(manifest):
            raise OSError("Manifest file not exists: %s" % manifest)

        image_packages = set()
        with open(manifest, 'r') as f:
            for line in f.readlines():
                line = line.strip()
                if line and not line.startswith("#"):
                    image_packages.add(line.split()[0])

        return image_packages

    @staticmethod
    def getHostDumper(cmds, directory):
        return HostDumper(cmds, directory)

    def _process_args(self, logger, args):
        if not args.packages_manifest:
            raise TypeError('Manifest file not provided')

        super(OERuntimeTestContextExecutor, self)._process_args(logger, args)

        target_kwargs = {}
        target_kwargs['qemuboot'] = args.qemu_boot

        self.tc_kwargs['init']['target'] = \
                OERuntimeTestContextExecutor.getTarget(args.target_type,
                        None, args.target_ip, args.server_ip, **target_kwargs)
        self.tc_kwargs['init']['host_dumper'] = \
                OERuntimeTestContextExecutor.getHostDumper(None,
                        args.host_dumper_dir)
        self.tc_kwargs['init']['image_packages'] = \
                OERuntimeTestContextExecutor.readPackagesManifest(
                        args.packages_manifest)
        self.tc_kwargs['init']['extract_dir'] = args.extract_dir

_executor_class = OERuntimeTestContextExecutor
