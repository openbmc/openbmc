#
# Copyright (C) 2023-2024 Siemens AG
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool ide-sdk IDE plugin interface definition and helper functions"""

import errno
import json
import logging
import os
from enum import Enum, auto
from devtool import DevtoolError

logger = logging.getLogger('devtool')


class BuildTool(Enum):
    UNDEFINED = auto()
    CMAKE = auto()
    MESON = auto()
    KERNEL_MODULE = auto()

    @property
    def is_c_ccp(self):
        if self is BuildTool.CMAKE:
            return True
        if self is BuildTool.MESON:
            return True
        return False

    @property
    def is_c_cpp_kernel(self):
        if self.is_c_ccp or self is BuildTool.KERNEL_MODULE:
            return True
        return False


class GdbServerModes(Enum):
    ONCE = auto()
    ATTACH = auto()
    MULTI = auto()


class GdbCrossConfig:
    """Base class defining the GDB configuration generator interface

    Generate a GDB configuration for a binary on the target device.
    """
    _gdbserver_port_next = 1234
    _gdb_cross_configs = {}

    def __init__(self, image_recipe, modified_recipe, binary, gdbserver_default_mode):
        self.image_recipe = image_recipe
        self.modified_recipe = modified_recipe
        self.gdb_cross = modified_recipe.gdb_cross
        self.binary = binary
        self.gdbserver_default_mode = gdbserver_default_mode
        self.binary_pretty = self.binary.binary_path.replace(os.sep, '-').lstrip('-')
        self.gdbserver_port = GdbCrossConfig._gdbserver_port_next
        GdbCrossConfig._gdbserver_port_next += 1
        self.id_pretty = "%d_%s" % (self.gdbserver_port, self.binary_pretty)

        # Track all generated gdbserver configs to avoid duplicates
        if self.id_pretty in GdbCrossConfig._gdb_cross_configs:
            raise DevtoolError(
                "gdbserver config for binary %s is already generated" % binary)
        GdbCrossConfig._gdb_cross_configs[self.id_pretty] = self

    def id_pretty_mode(self, gdbserver_mode):
        return "%s_%s" % (self.id_pretty, gdbserver_mode.name.lower())

    # GDB and gdbserver script on the host
    @property
    def script_dir(self):
        return self.modified_recipe.ide_sdk_scripts_dir

    @property
    def gdbinit_dir(self):
        return os.path.join(self.script_dir, 'gdbinit')

    def gdbserver_script_file(self, gdbserver_mode):
        return 'gdbserver_' + self.id_pretty_mode(gdbserver_mode)

    def gdbserver_script(self, gdbserver_mode):
        return os.path.join(self.script_dir, self.gdbserver_script_file(gdbserver_mode))

    @property
    def gdbinit(self):
        return os.path.join(
            self.gdbinit_dir, 'gdbinit_' + self.id_pretty)

    @property
    def gdb_script(self):
        return os.path.join(
            self.script_dir, 'gdb_' + self.id_pretty)

    # gdbserver files on the target
    def gdbserver_tmp_dir(self, gdbserver_mode):
        return os.path.join('/tmp', 'gdbserver_%s' % self.id_pretty_mode(gdbserver_mode))

    def gdbserver_pid_file(self, gdbserver_mode):
        return os.path.join(self.gdbserver_tmp_dir(gdbserver_mode), 'gdbserver.pid')

    def gdbserver_log_file(self, gdbserver_mode):
        return os.path.join(self.gdbserver_tmp_dir(gdbserver_mode), 'gdbserver.log')

    def _target_gdbserver_start_cmd(self, gdbserver_mode):
        """Get the ssh command to start gdbserver on the target device

        returns something like:
          "\"/bin/sh -c '/usr/bin/gdbserver --once :1234 /usr/bin/cmake-example'\""
        or for multi mode:
          "\"/bin/sh -c 'if [ \"$1\" = \"stop\" ]; then ... else ... fi'\""
        """
        if gdbserver_mode == GdbServerModes.ONCE:
            gdbserver_cmd_start = "%s --once :%s %s" % (
                self.gdb_cross.gdbserver_path, self.gdbserver_port, self.binary.binary_path)
        elif gdbserver_mode == GdbServerModes.ATTACH:
            pid_command = self.binary.pid_command
            if pid_command:
                gdbserver_cmd_start = "%s --attach :%s \\$(%s)" % (
                    self.gdb_cross.gdbserver_path,
                    self.gdbserver_port,
                    pid_command)
            else:
                raise DevtoolError("Cannot use gdbserver attach mode for binary %s. No PID found." % self.binary.binary_path)
        elif gdbserver_mode == GdbServerModes.MULTI:
            gdbserver_cmd_start = "test -f %s && exit 0; " % self.gdbserver_pid_file(gdbserver_mode)
            gdbserver_cmd_start += "mkdir -p %s; " % self.gdbserver_tmp_dir(gdbserver_mode)
            gdbserver_cmd_start += "%s --multi :%s > %s 2>&1 & " % (
                self.gdb_cross.gdbserver_path, self.gdbserver_port, self.gdbserver_log_file(gdbserver_mode))
            gdbserver_cmd_start += "echo \\$! > %s;" % self.gdbserver_pid_file(gdbserver_mode)
        else:
            raise DevtoolError("Unsupported gdbserver mode: %s" % gdbserver_mode)
        return "\"/bin/sh -c '" + gdbserver_cmd_start + "'\""

    def _target_gdbserver_kill_cmd(self):
        """Get the ssh command to kill gdbserver on the target device"""
        return "\"kill \\$(pgrep -o -f 'gdbserver --attach :%s') 2>/dev/null || true\"" % self.gdbserver_port

    def _target_ssh_gdbserver_args(self):
        ssh_args = []
        if self.gdb_cross.target_device.ssh_port:
            ssh_args += ["-p", self.gdb_cross.target_device.ssh_port]
        if self.gdb_cross.target_device.extraoptions:
            ssh_args.extend(self.gdb_cross.target_device.extraoptions)
        if self.gdb_cross.target_device.target:
            ssh_args.append(self.gdb_cross.target_device.target)
        return ssh_args

    def gdbserver_modes(self):
        """Get the list of gdbserver modes for which scripts are generated"""
        modes = [self.gdbserver_default_mode]
        if self.binary.runs_as_service and self.gdbserver_default_mode != GdbServerModes.ATTACH:
            modes.append(GdbServerModes.ATTACH)
        return modes

    def initialize(self):
        """Interface function to initialize the gdb config generation"""
        pass



class IdeBase:
    """Base class defining the interface for IDE plugins"""

    def __init__(self):
        self.ide_name = 'undefined'
        self.gdb_cross_configs = []

    @classmethod
    def ide_plugin_priority(cls):
        """Used to find the default ide handler if --ide is not passed"""
        return 10

    def setup_shared_sysroots(self, shared_env):
        logger.warn("Shared sysroot mode is not supported for IDE %s" %
                    self.ide_name)

    def setup_modified_recipe(self, args, image_recipe, modified_recipe):
        logger.warn("Modified recipe mode is not supported for IDE %s" %
                    self.ide_name)

    def initialize_gdb_cross_configs(self, image_recipe, modified_recipe, gdb_cross_config_class=GdbCrossConfig):
        for _, exec_bin in modified_recipe.installed_binaries.items():
            gdb_cross_config = gdb_cross_config_class(
                image_recipe, modified_recipe, exec_bin)
            gdb_cross_config.initialize()
            self.gdb_cross_configs.append(gdb_cross_config)

    @staticmethod
    def gen_oe_scripts_sym_link(modified_recipe):
        # create a sym-link from sources to the scripts directory
        if os.path.isdir(modified_recipe.ide_sdk_scripts_dir):
            IdeBase.symlink_force(modified_recipe.ide_sdk_scripts_dir,
                                  os.path.join(modified_recipe.real_srctree, 'oe-scripts'))

    @staticmethod
    def update_json_file(json_dir, json_file, update_dict):
        """Update a json file

        By default it uses the dict.update function. If this is not sutiable
        the update function might be passed via update_func parameter.
        """
        json_path = os.path.join(json_dir, json_file)
        logger.info("Updating IDE config file: %s (%s)" %
                    (json_file, json_path))
        if not os.path.exists(json_dir):
            os.makedirs(json_dir)
        try:
            with open(json_path) as f:
                orig_dict = json.load(f)
        except json.decoder.JSONDecodeError:
            logger.info(
                "Decoding %s failed. Probably because of comments in the json file" % json_path)
            orig_dict = {}
        except FileNotFoundError:
            orig_dict = {}
        orig_dict.update(update_dict)
        with open(json_path, 'w') as f:
            json.dump(orig_dict, f, indent=4)

    @staticmethod
    def symlink_force(tgt, dst):
        try:
            os.symlink(tgt, dst)
        except OSError as err:
            if err.errno == errno.EEXIST:
                if os.readlink(dst) != tgt:
                    os.remove(dst)
                    os.symlink(tgt, dst)
            else:
                raise err


def get_devtool_deploy_opts(args):
    """Filter args for devtool deploy-target args"""
    if not args.target:
        return None
    devtool_deploy_opts = [args.target]
    if args.no_host_check:
        devtool_deploy_opts += ["-c"]
    if args.show_status:
        devtool_deploy_opts += ["-s"]
    if args.no_preserve:
        devtool_deploy_opts += ["-p"]
    if args.no_check_space:
        devtool_deploy_opts += ["--no-check-space"]
    if args.ssh_exec:
        devtool_deploy_opts += ["-e", args.ssh.exec]
    if args.port:
        devtool_deploy_opts += ["-P", args.port]
    if args.key:
        devtool_deploy_opts += ["-I", args.key]
    if args.strip is False:
        devtool_deploy_opts += ["--no-strip"]
    return devtool_deploy_opts
