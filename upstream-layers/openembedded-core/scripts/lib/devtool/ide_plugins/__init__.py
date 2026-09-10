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
    def is_c_cpp(self):
        if self is BuildTool.CMAKE:
            return True
        if self is BuildTool.MESON:
            return True
        return False

    @property
    def is_c_cpp_kernel(self):
        if self.is_c_cpp or self is BuildTool.KERNEL_MODULE:
            return True
        return False


class DebuggerServerModes(Enum):
    ONCE = auto()
    ATTACH = auto()
    MULTI = auto()


class DebuggerCrossConfig:
    """Base class defining the cross-debugger configuration generator interface.

    Manages the per-binary port assignment, script paths, and SSH argument
    construction that are common to all debugger back-ends (GDB, LLDB).
    Concrete subclasses provide the back-end-specific remote start/kill commands.
    """
    _port_next = 1234
    _configs = {}

    def __init__(self, image_recipe, modified_recipe, binary, default_mode):
        self.image_recipe = image_recipe
        self.modified_recipe = modified_recipe
        self.debugger_cross = modified_recipe.debugger_cross
        self.binary = binary
        self.default_mode = default_mode
        self.binary_pretty = self.binary.binary_path.replace(os.sep, '-').lstrip('-')
        self.debug_server_ports = {}
        for mode in self.server_modes():
            self.debug_server_ports[mode] = DebuggerCrossConfig._port_next
            DebuggerCrossConfig._port_next += 1
        self.debug_server_port = self.debug_server_ports[self.default_mode]
        self.id_pretty = "%d_%s" % (self.debug_server_port, self.binary_pretty)
        # Hook for subclasses needing additional fixed ports forwarded through
        # slirp beyond the one-per-mode debug_server_ports (e.g. lldb-server's
        # spawned gdbserver instances).
        self.extra_ports = []

        if self.id_pretty in DebuggerCrossConfig._configs:
            raise DevtoolError(
                "debugger config for binary %s is already generated" % binary)
        DebuggerCrossConfig._configs[self.id_pretty] = self

    def port(self, mode=None):
        """Return the debug server port allocated for a server mode."""
        if mode is None:
            mode = self.default_mode
        return self.debug_server_ports[mode]

    def id_pretty_mode(self, mode):
        return "%d_%s_%s" % (self.port(mode), self.binary_pretty, mode.name.lower())

    # Host-side script paths
    @property
    def script_dir(self):
        return self.modified_recipe.ide_sdk_scripts_dir

    def server_script(self, mode):
        raise NotImplementedError

    # SSH argument helpers
    def _target_ssh_args(self):
        ssh_args = []
        if self.debugger_cross.target_device.ssh_port:
            ssh_args += self.debugger_cross.target_device.ssh_port
        if self.debugger_cross.target_device.extraoptions:
            ssh_args.extend(self.debugger_cross.target_device.extraoptions)
        if self.debugger_cross.target_device.target:
            ssh_args.append(self.debugger_cross.target_device.target)
        return ssh_args

    def server_modes(self):
        """List of debug-server modes for which scripts are generated."""
        modes = [self.default_mode]
        if self.binary.runs_as_service and self.default_mode != DebuggerServerModes.ATTACH:
            modes.append(DebuggerServerModes.ATTACH)
        return modes

    # Re-tries in 0.1s Example: 300 * 0.1s, i.e. ~30s.
    TARGET_START_RETRIES = 300

    def _target_tcp_port_check_cmd(self, mode=None):
        hex_port = "%04X" % self.port(mode)
        return "grep -q :%s /proc/net/tcp /proc/net/tcp6 2>/dev/null" % hex_port

    def _target_wait_for_tcp_port_cmd(self, pid_var=None, log_file=None, mode=None):
        """Shell fragment waiting until the debug server listens on its port.

        The server log is dumped to stderr when giving up.
        """
        port = self.port(mode)
        dump_log = "cat %s >&2; " % log_file if log_file else ""
        cleanup = ""
        died_check = ""
        if pid_var:
            cleanup = "kill \\$_%s 2>/dev/null; " % pid_var
            died_check = (
                "kill -0 \\$_%s 2>/dev/null || { %secho %s exited before it started listening on port %s >&2; exit 1; }; "
                    % (pid_var, dump_log, self.DEBUG_SERVER_NAME, port))
        return (
            "_w=0; while ! %s; do %s_w=\\$((_w+1)); [ \\$_w -lt %d ] || { "
            "%secho %s did not start on port %s after \\$_w retries >&2; %sexit 1; }; "
            "sleep 0.1; done;"
                % (self._target_tcp_port_check_cmd(mode), died_check, self.TARGET_START_RETRIES,
                    cleanup, self.DEBUG_SERVER_NAME, port, dump_log))

    def _target_wait_for_process_exit_cmd(self, pid_var):
        return (
            "_w=0; while kill -0 \\$_%s 2>/dev/null; do _w=\\$((_w+1)); "
            "[ \\$_w -lt 100 ] || { echo %s did not stop >&2; exit 1; }; "
            "sleep 0.1; done;"
            % (pid_var, self.DEBUG_SERVER_NAME))

    def initialize(self):
        """Called after construction to generate any required config files."""
        pass

    # Abstract — subclasses must implement
    def _target_start_cmd(self, mode):
        raise NotImplementedError

    def _target_stop_cmd(self, mode):
        raise NotImplementedError


class GdbCrossConfig(DebuggerCrossConfig):
    """GDB-specific cross-debugging configuration.

    Manages gdbserver on the target and gdb-cross on the host.  Provides
    gdbinit / gdb wrapper scripts used by ide=none as well as the
    target-side tmp/pid/log paths consumed by the gdbserver start command.
    """
    DEBUG_SERVER_NAME = "gdbserver"

    def __init__(self, image_recipe, modified_recipe, binary,
                 default_mode=DebuggerServerModes.MULTI):
        super().__init__(image_recipe, modified_recipe, binary,
                         default_mode)

    # GDB-specific host paths
    @property
    def gdbinit_dir(self):
        return os.path.join(self.script_dir, 'gdbinit')

    @property
    def gdbinit(self):
        return os.path.join(self.gdbinit_dir, 'gdbinit_' + self.id_pretty)

    @property
    def gdb_script(self):
        return os.path.join(self.script_dir, 'gdb_' + self.id_pretty)

    def server_script_file(self, mode):
        return 'gdbserver_' + self.id_pretty_mode(mode)

    def server_script(self, mode):
        return os.path.join(self.script_dir, self.server_script_file(mode))

    # gdbserver files on the target
    def _gdbserver_tmp_dir(self, mode):
        return os.path.join('/tmp', 'gdbserver_%s' % self.id_pretty_mode(mode))

    def _gdbserver_pid_file(self, mode):
        return os.path.join(self._gdbserver_tmp_dir(mode), 'gdbserver.pid')

    def _gdbserver_log_file(self, mode):
        return os.path.join(self._gdbserver_tmp_dir(mode), 'gdbserver.log')

    def _target_start_cmd(self, server_mode):
        """SSH command to start gdbserver on the target device.

        Returns something like:
          "\"/bin/sh -c '/usr/bin/gdbserver --once :1234 /usr/bin/cmake-example'\""
        """
        port = self.port(server_mode)
        if server_mode == DebuggerServerModes.ONCE:
            # gdbserver runs directly in the foreground for the whole debug
            # session and exits by itself once it ends. Its own "Listening
            # on port ..." message (matched by get_debug_server_ready_pattern())
            # is the readiness signal, so no backgrounding, PID file or
            # separate polling loop is needed here.
            gdbserver_cmd_start = "%s --once :%s %s" % (
                self.debugger_cross.debug_server_path, port, self.binary.binary_path)
        elif server_mode in (DebuggerServerModes.ATTACH, DebuggerServerModes.MULTI):
            # Both modes run a persistent server speaking the extended-remote
            # protocol. They differ on the client side only: ATTACH attaches to
            # a process that is already running on the target.
            pid_file = self._gdbserver_pid_file(server_mode)
            log_file = self._gdbserver_log_file(server_mode)
            # Reuse an already-running server for this configuration instead
            # of starting a second one on the same port.
            gdbserver_cmd_start = "if test -f %s && kill -0 \\$(cat %s) 2>/dev/null; then exit 0; fi; " % (
                pid_file, pid_file)
            gdbserver_cmd_start += "mkdir -p %s; " % self._gdbserver_tmp_dir(server_mode)
            gdbserver_cmd_start += "%s --multi :%s > %s 2>&1 & _gdbserver_pid=\\$!; " % (
                self.debugger_cross.debug_server_path, port, log_file)
            gdbserver_cmd_start += "echo \\$_gdbserver_pid > %s; " % pid_file
            gdbserver_cmd_start += self._target_wait_for_tcp_port_cmd(
                "gdbserver_pid", mode=server_mode)
        else:
            raise DevtoolError("Unsupported gdbserver mode: %s" % server_mode)
        return "\"/bin/sh -c '" + gdbserver_cmd_start + "'\""

    def get_debug_server_ready_pattern(self, mode=None):
        """Regex matching gdbserver's own "Listening on port N" startup message.

        Used as the problemMatcher endsPattern for VS Code and by selftests to
        detect readiness directly from gdbserver's output, instead of a
        separate polling probe.
        """
        return r"^Listening on port %d$" % self.port(mode)

    def _target_stop_cmd(self, server_mode):
        """SSH command to stop gdbserver on the target device.

        Stopping is based on the PID file written by the start command. Other
        debug sessions run their own gdbserver on the target, so anything
        matching by process name would hit them as well.
        """
        pid_file = self._gdbserver_pid_file(server_mode)
        gdbserver_cmd_stop = "if test -f %s; then _gdbserver_pid=\\$(cat %s); " % (
            pid_file, pid_file)
        gdbserver_cmd_stop += "kill \\$_gdbserver_pid 2>/dev/null; "
        gdbserver_cmd_stop += self._target_wait_for_process_exit_cmd(
            "gdbserver_pid")
        gdbserver_cmd_stop += " fi; rm -rf %s" % self._gdbserver_tmp_dir(server_mode)
        return "\"/bin/sh -c '" + gdbserver_cmd_stop + "'\""


class LldbServerConfig(DebuggerCrossConfig):
    """Configure lldb-server (platform mode) on the target for CodeLLDB remote debugging.

    Unlike gdbserver, lldb-server platform mode is architecture-agnostic on the host
    side: a single lldb-native binary handles all target architectures via the
    LLDB platform protocol that CodeLLDB speaks natively.
    """
    DEBUG_SERVER_NAME = "lldb-server"
    TARGET_START_RETRIES = 600

    def __init__(self, image_recipe, modified_recipe, binary,
                 default_mode=DebuggerServerModes.MULTI):
        super().__init__(image_recipe, modified_recipe, binary,
                         default_mode)
        # lldb-server platform spawns a separate gdb-remote-protocol
        # "gdbserver" instance per debug session; without --gdbserver-port it
        # picks a random port, which cannot be forwarded through slirp NAT.
        # Pin a fixed, dedicated port per mode, each gets slirp-forwarded too.
        self.gdbserver_ports = {}
        for mode in self.server_modes():
            self.gdbserver_ports[mode] = DebuggerCrossConfig._port_next
            DebuggerCrossConfig._port_next += 1
            self.extra_ports.append(self.gdbserver_ports[mode])

    def _lldb_server_tmp_dir(self, mode):
        return os.path.join('/tmp', 'lldb_server_%s' % self.id_pretty_mode(mode))

    def _lldb_server_pid_file(self, mode):
        return os.path.join(self._lldb_server_tmp_dir(mode), 'lldb_server.pid')

    def _lldb_server_log_file(self, mode):
        return os.path.join(self._lldb_server_tmp_dir(mode), 'lldb_server.log')

    def _target_start_cmd(self, mode):
        """SSH command to start lldb-server in platform mode on the target.

        Used identically for MULTI and ATTACH: in both cases lldb-server just
        offers a platform connection, it does not care whether the client that
        connects to it goes on to launch a new process or attach to an
        existing one.
        """
        if mode not in (DebuggerServerModes.MULTI, DebuggerServerModes.ATTACH):
            raise DevtoolError("Unsupported lldb-server mode: %s" % mode)
        lldb_server = self.debugger_cross.debug_server_path
        # Use '*:<port>' so lldb-server binds on all interfaces (0.0.0.0), not
        # just loopback.  The bare ':<port>' form only binds to 127.0.0.1 in
        # lldb-server 21.x and the remote lldb client connects from the host.
        # Start from /tmp because lldb-server creates temp files in its cwd and
        # the SSH default cwd (/home/root) may not exist on a minimal image.
        pid_file = self._lldb_server_pid_file(mode)
        tmp_dir = self._lldb_server_tmp_dir(mode)
        log_file = self._lldb_server_log_file(mode)
        cmd = self._target_tcp_port_check_cmd(mode) + " && exit 0; "
        cmd += "mkdir -p %s; " % tmp_dir
        cmd += "cd %s; " % tmp_dir
        cmd += "%s platform --server --listen *:%s --gdbserver-port %s > %s 2>&1 & _lldb_server_pid=\\$!; " % (
            lldb_server, self.port(mode), self.gdbserver_ports[mode], log_file)
        cmd += "echo \\$_lldb_server_pid > %s; " % pid_file
        cmd += self._target_wait_for_tcp_port_cmd(
            "lldb_server_pid", log_file, mode)
        return "\"/bin/sh -c '" + cmd + "'\""

    def _target_stop_cmd(self, server_mode):
        """SSH command to stop the lldb-server platform instance for the given mode."""
        pid_file = self._lldb_server_pid_file(server_mode)
        tmp_dir = self._lldb_server_tmp_dir(server_mode)
        cmd = ("test -f %(pf)s && kill \\$(cat %(pf)s) 2>/dev/null; rm -rf %(td)s"
               % {'pf': pid_file, 'td': tmp_dir})
        return "\"/bin/sh -c '" + cmd + "'\""

    def server_script_file(self, mode):
        return 'lldb_server_' + self.id_pretty_mode(mode)

    def server_script(self, mode):
        return os.path.join(self.script_dir, self.server_script_file(mode))

class IdeBase:
    """Base class defining the interface for IDE plugins"""

    def __init__(self):
        self.ide_name = 'undefined'
        self.cross_debug_configs = []

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

    def initialize_cross_debug_configs(self, image_recipe, modified_recipe, cross_debug_config_class=GdbCrossConfig):
        for _, exec_bin in modified_recipe.installed_binaries.items():
            cross_debug_config = cross_debug_config_class(
                image_recipe, modified_recipe, exec_bin)
            cross_debug_config.initialize()
            self.cross_debug_configs.append(cross_debug_config)

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


def resolve_deploy_target(args, target_override=None):
    """Resolve the effective devtool deploy-target destination.

    target_override, e.g. the local NFS rootfs directory when --nfs was used
    (see devtool.deploy for how a directory target is handled without ssh),
    always takes precedence over the ssh target from -t/--target.
    """
    target = target_override or args.target
    if not target:
        raise DevtoolError('No deploy target, pass -t/--target or --nfs')
    return target


def get_devtool_deploy_opts(args, target_override=None):
    """Filter args for devtool deploy-target args"""
    devtool_deploy_opts = [resolve_deploy_target(args, target_override)]
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
    for package in (args.package or []):
        devtool_deploy_opts += ["--package", package]
    for file_glob in (args.file_globs or []):
        devtool_deploy_opts += ["--file-glob", file_glob]
    return devtool_deploy_opts
