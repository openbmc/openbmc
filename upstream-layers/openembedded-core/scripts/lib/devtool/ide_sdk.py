# Development tool - ide-sdk command plugin
#
# Copyright (C) 2023-2024 Siemens AG
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool ide-sdk plugin"""

import json
import logging
import os
import re
import shutil
import stat
import subprocess
import sys
import shlex
import glob
from argparse import RawTextHelpFormatter
from enum import Enum
from pathlib import Path

import scriptutils
import bb
from devtool import exec_build_env_command, setup_tinfoil, check_workspace_recipe, DevtoolError, parse_recipe
from devtool.standard import get_real_srctree
from devtool.deploy import parse_packages_arg
from devtool.ide_plugins import BuildTool, DebuggerCrossConfig
from oe.kernel_module import kernel_module_os_env
from pseudo_rootfs_utils import PseudoRootfsError, extract_sdk_rootfs, pseudo_state_dir


logger = logging.getLogger('devtool')

# dict of classes derived from IdeBase
ide_plugins = {}


class DevtoolIdeMode(Enum):
    """Different modes are supported by the ide-sdk plugin.

    The enum might be extended by more advanced modes in the future. Some ideas:
    - auto: modified if all recipes are modified, shared if none of the recipes is modified.
    - mixed: modified mode for modified recipes, shared mode for all other recipes.
    """

    modified = 'modified'
    shared = 'shared'


# Hosts a ssh target is considered to loop back to the local machine, e.g. a
# QEMU instance reached through slirp/hostfwd port forwarding (root@localhost)
# which has an ephemeral ssh host key that changes on every boot.
LOOPBACK_HOSTS = ('localhost', '127.0.0.1', '::1')


def target_host(target):
    return target.split('@')[-1]


def is_loopback_target(target):
    return target_host(target) in LOOPBACK_HOSTS


class TargetDevice:
    """SSH remote login parameters"""

    def __init__(self, args):
        self.target = args.target
        target_sp = args.target.split('@')
        if len(target_sp) == 1:
            self.login = ""
            self.host = target_sp[0]
        elif len(target_sp) == 2:
            self.login = target_sp[0]
            self.host = target_sp[1]
        else:
            logger.error("Invalid target argument: %s" % args.target)

        no_host_check = args.no_host_check
        if not no_host_check and is_loopback_target(args.target):
            logger.debug(
                "Target %s is a loopback address, disabling ssh host key checking "
                "(assuming a QEMU instance with an ephemeral host key)." % args.target)
            no_host_check = True

        self.extraoptions = []
        if no_host_check:
            self.extraoptions += ['-o', 'UserKnownHostsFile=/dev/null', '-o', 'StrictHostKeyChecking=no']
        self.ssh_sshexec = 'ssh'
        if args.ssh_exec:
            self.ssh_sshexec = args.ssh_exec
        self.ssh_port = []
        if args.port:
            self.ssh_port = ['-p', args.port]
        if args.key:
            self.extraoptions += ['-i', args.key]


class RecipeNative:
    """Base class for calling bitbake to provide a -native recipe"""

    def __init__(self, name, target_arch=None):
        self.name = name
        self.target_arch = target_arch
        self.bootstrap_tasks = [self.name + ':do_addto_recipe_sysroot']
        self.staging_bindir_native = None
        self.target_sys = None
        self.__native_bin = None

    def _initialize(self, config, workspace, tinfoil):
        """Get the parsed recipe"""
        recipe_d = parse_recipe(
            config, tinfoil, self.name, appends=True, filter_workspace=False)
        if not recipe_d:
            raise DevtoolError("Parsing %s recipe failed" % self.name)
        self.staging_bindir_native = os.path.realpath(
            recipe_d.getVar('STAGING_BINDIR_NATIVE'))
        self.target_sys = recipe_d.getVar('TARGET_SYS')
        return recipe_d

    def initialize(self, config, workspace, tinfoil):
        """Basic initialization that can be overridden by a derived class"""
        self._initialize(config, workspace, tinfoil)

    @property
    def native_bin(self):
        if not self.__native_bin:
            raise DevtoolError("native binary name is not defined.")
        return self.__native_bin


class RecipeGdbCross(RecipeNative):
    """Handle gdb-cross on the host and the gdbserver on the target device"""

    def __init__(self, args, target_arch, target_device):
        super().__init__('gdb-cross-' + target_arch, target_arch)
        self.target_device = target_device
        self.gdb = None
        self.gdbserver_port_next = int(args.gdbserver_port_start)
        self.config_db = {}

    def __find_gdbserver(self, config, tinfoil):
        """Absolute path of the gdbserver"""
        recipe_d_gdb = parse_recipe(
            config, tinfoil, 'gdb', appends=True, filter_workspace=False)
        if not recipe_d_gdb:
            raise DevtoolError("Parsing gdb recipe failed")
        return os.path.join(recipe_d_gdb.getVar('bindir'), 'gdbserver')

    def initialize(self, config, workspace, tinfoil):
        super()._initialize(config, workspace, tinfoil)
        gdb_bin = self.target_sys + '-gdb'
        gdb_path = os.path.join(
            self.staging_bindir_native, self.target_sys, gdb_bin)
        self.gdb = gdb_path
        self.debug_server_path = self.__find_gdbserver(config, tinfoil)

    @property
    def host(self):
        return self.target_device.host


class RecipeLldbNative(RecipeNative):
    """Handle lldb on the host and lldb-server on the target device.

    Unlike GDB which requires a per-architecture gdb-cross-<arch> binary, LLDB
    is architecture-agnostic: a single lldb-native installation can debug any
    target architecture via the LLDB platform protocol.

    On the target side, lldb-server (the ${PN}-server sub-package from the lldb
    recipe) provides the platform server that CodeLLDB connects to.
    """

    def __init__(self, args, target_device):
        super().__init__('lldb-native')
        self.target_device = target_device
        self.lldb = None
        self._lldb_server_path = None

    def __find_lldb_server(self, config, tinfoil):
        """Absolute path of lldb-server on the target (from the lldb recipe)."""
        recipe_d_lldb = parse_recipe(
            config, tinfoil, 'lldb', appends=True, filter_workspace=False)
        if not recipe_d_lldb:
            raise DevtoolError("Parsing lldb recipe failed")
        return os.path.join(recipe_d_lldb.getVar('bindir'), 'lldb-server')

    def initialize(self, config, workspace, tinfoil):
        super()._initialize(config, workspace, tinfoil)
        self.lldb = os.path.join(self.staging_bindir_native, 'lldb')
        self._lldb_server_path = self.__find_lldb_server(config, tinfoil)

    @property
    def debug_server_path(self):
        return self._lldb_server_path

    @property
    def host(self):
        return self.target_device.host


class RecipeImage:
    """Handle some image recipe related properties

    Most workflows require firmware that runs on the target device.
    This firmware must be consistent with the setup of the host system.
    In particular, the debug symbols must be compatible. For this, the
    rootfs must be created as part of the SDK.
    """

    MARKER = '# devtool ide-sdk: image debug settings'
    QB_SLIRP_MARKER = '# devtool ide-sdk: QB_SLIRP_OPT'

    def __init__(self, name, orig_bbappend_content=None):
        self.name = name
        self.pn = None
        self.__rootfs = None
        self.__rootfs_dbg = None
        self.__nfs_rootfs = None
        self.__nfs_rootfs_dbg = None
        self.nfs_deploy_dir = None
        self.deploy_dir_image = None
        self.image_link_name = None
        self.qb_slirp_opt = ''
        self.fakerootcmd = None
        self.fakerootenv = None
        self.bootstrap_tasks = [self.name + ':do_build']
        # Debug settings already provided by the base configuration (e.g.
        # local.conf, MACHINE, DISTRO, the recipe itself) plus any bbappend
        # content other than devtool ide-sdk's own sections (see
        # strip_bbappend_sections()). Populated by initialize().
        self.base_image_gen_debugfs = False
        self.base_image_fstypes = set()
        self.base_image_fstypes_debugfs = ''
        self.base_has_combined_dbg = False
        self.base_image_install = set()
        self._bbappend = None
        # Content of the bbappend before strip_bbappend_sections() ran.
        self._orig_bbappend_content = orig_bbappend_content

    @staticmethod
    def _strip_marker_section(content, marker):
        """Remove one devtool ide-sdk marker section, if present"""
        return re.sub(
            r'^' + re.escape(marker) + r'\n(?:[^\n]+\n)*',
            '', content, flags=re.MULTILINE)

    @classmethod
    def strip_bbappend_sections(cls, config, recipe_names):
        """Remove devtool ide-sdk's own bbappend section from earlier runs

        Returns a {recipe name: content before stripping} dict for the recipes
        that have a bbappend, to be passed on to the RecipeImage constructor.
        """
        originals = {}
        appends_dir = os.path.join(config.workspace_path, 'appends')
        for name in recipe_names:
            bbappend = os.path.join(appends_dir, name + '.bbappend')
            if not os.path.exists(bbappend):
                continue
            with open(bbappend, 'r') as f:
                content = f.read()
            originals[name] = content
            stripped = cls._strip_marker_section(content, cls.MARKER)
            stripped = cls._strip_marker_section(stripped, cls.QB_SLIRP_MARKER)
            if stripped != content:
                with open(bbappend, 'w') as f:
                    f.write(stripped)
        return originals

    def initialize(self, config, tinfoil):
        appends_dir = os.path.join(config.workspace_path, 'appends')
        self._bbappend = os.path.join(appends_dir, self.name + '.bbappend')

        # strip_bbappend_sections() ran before the tinfoil session started, so
        # this parse sees the same bbappend content as any other parse of this
        # recipe until the tinfoil session ends.
        image_d = parse_recipe(
            config, tinfoil, self.name, appends=True, filter_workspace=False)
        if not image_d:
            raise DevtoolError(
                "Parsing image recipe %s failed" % self.name)

        self.pn = image_d.getVar('PN')
        self.base_image_gen_debugfs = image_d.getVar(
            'IMAGE_GEN_DEBUGFS') == '1'
        self.base_image_fstypes = set(
            (image_d.getVar('IMAGE_FSTYPES') or '').split())
        self.base_image_fstypes_debugfs = image_d.getVar(
            'IMAGE_FSTYPES_DEBUGFS') or ''
        self.base_has_combined_dbg = bb.data.inherits_class(
            'image-combined-dbg', image_d)
        self.base_image_install = set(
            (image_d.getVar('IMAGE_INSTALL') or '').split())

        workdir = image_d.getVar('WORKDIR')
        self.__rootfs = os.path.join(workdir, 'rootfs')
        self.__rootfs_dbg = os.path.join(workdir, 'rootfs-dbg')

        self.deploy_dir_image = image_d.getVar('DEPLOY_DIR_IMAGE')
        self.image_link_name = image_d.getVar('IMAGE_LINK_NAME')
        self.qb_slirp_opt = image_d.getVar('QB_SLIRP_OPT') or ''
        self.fakerootcmd = image_d.getVar('FAKEROOTCMD')
        self.fakerootenv = image_d.getVar('FAKEROOTENV')

    @property
    def debug_support(self):
        return bool(self.rootfs_dbg)

    @property
    def rootfs(self):
        """Prefer the live NFS-exported rootfs (if --nfs=rootfs is used) over the
        static WORKDIR/rootfs left over from the image build, so solib_search_path()
        finds files as devtool deploy-target actually updates them."""
        if self.__nfs_rootfs:
            return self.__nfs_rootfs
        return self.__rootfs

    @property
    def rootfs_dbg(self):
        if self.__nfs_rootfs_dbg:
            return self.__nfs_rootfs_dbg
        if self.__rootfs_dbg and os.path.isdir(self.__rootfs_dbg):
            return self.__rootfs_dbg
        return None

    def set_nfs_rootfs(self, nfs_export_base_dir, nfs):
        """Select the NFS rootfs for generated debugger paths and deploys."""
        if not nfs:
            return
        self.nfs_deploy_dir = self.nfs_rootfs_dir(nfs_export_base_dir, nfs)
        if nfs == 'rootfs-dbg':
            self.__nfs_rootfs_dbg = self.nfs_deploy_dir
        elif nfs == 'rootfs':
            self.__nfs_rootfs = self.nfs_deploy_dir

    def nfs_rootfs_dir(self, nfs_export_base_dir, nfs):
        """Return the directory for the selected NFS rootfs, below nfs_export_base_dir."""
        return os.path.join(nfs_export_base_dir, self.pn, nfs)

    def nfs_runqemu_helper(self, nfs_export_base_dir, nfs):
        """Create a helper that boots the selected rootfs through runqemu."""
        export_dir = os.path.join(nfs_export_base_dir, self.pn)
        rootfs_dir = self.nfs_rootfs_dir(nfs_export_base_dir, nfs)
        qemuboot = os.path.join(
            self.deploy_dir_image, self.image_link_name + '.qemuboot.conf')
        if not os.path.exists(qemuboot):
            logger.info(
                'No qemuboot configuration was generated for %s; '
                'not creating a runqemu helper.', self.name)
            return None

        helper = os.path.join(export_dir, 'runqemu-' + nfs)
        with open(helper, 'w') as helper_file:
            helper_file.write('#!/bin/sh\n')
            helper_file.write(
                'exec runqemu %s %s "$@"\n' % (
                    shlex.quote(qemuboot), shlex.quote(rootfs_dir)))
        os.chmod(helper, os.stat(helper).st_mode | stat.S_IEXEC)
        return helper

    def update_image_bbappend(self, recipes_modified, nfs=None):
        """Write debug settings for modified-mode recipes into the image bbappend.

        Writes IMAGE_GEN_DEBUGFS, IMAGE_FSTYPES_DEBUGFS, IMAGE_CLASSES for
        image-combined-dbg, the appropriate debug server (gdbserver or
        lldb-server), and IMAGE_INSTALL entries for each modified recipe
        (including the ptest package when the recipe inherits ptest).

        initialize() already stripped this section from the bbappend on
        disk before parsing, so it only needs to be added back here, if
        still needed. Also updates QB_SLIRP_OPT with the debugger server
        port forwards (see update_qb_slirp_opt()). Returns True if the
        resulting bbappend content actually differs from what was on disk
        when initialize() ran, False if it is left exactly as it was.
        """
        wants_gdbserver = any(
            r.wants_gdbserver and r.toolchain != 'clang'
            for r in recipes_modified)
        wants_lldb_server = any(
            r.wants_gdbserver and r.toolchain == 'clang'
            for r in recipes_modified)

        # Only add what the base configuration (e.g. local.conf) does not
        # already provide, to avoid duplicate/conflicting settings.
        lines = []
        if not self.base_image_gen_debugfs:
            lines.append('IMAGE_GEN_DEBUGFS = "1"')
        if nfs == 'rootfs':
            if 'tar' not in self.base_image_fstypes:
                lines.append('IMAGE_FSTYPES:append = " tar"')
        elif nfs == 'rootfs-dbg':
            if self.base_image_fstypes_debugfs:
                if 'tar' not in self.base_image_fstypes_debugfs.split():
                    lines.append('IMAGE_FSTYPES_DEBUGFS:append = " tar"')
            else:
                lines.append('IMAGE_FSTYPES_DEBUGFS = "tar"')
        elif self.base_image_fstypes_debugfs != '':
            # Without --nfs no debug filesystem image is needed at all.
            lines.append('IMAGE_FSTYPES_DEBUGFS = ""')
        if not self.base_has_combined_dbg:
            lines.append('IMAGE_CLASSES += "image-combined-dbg"')
        if wants_gdbserver and 'gdbserver' not in self.base_image_install:
            lines.append('IMAGE_INSTALL:append = " gdbserver"')
        if wants_lldb_server and 'lldb-server' not in self.base_image_install:
            lines.append('IMAGE_INSTALL:append = " lldb-server"')
        for r in recipes_modified:
            if r.name not in self.base_image_install:
                lines.append('IMAGE_INSTALL:append = " %s"' % r.name)
            if r.has_ptest and (r.name + '-ptest') not in self.base_image_install:
                lines.append('IMAGE_INSTALL:append = " %s-ptest"' % r.name)

        original_content = self._orig_bbappend_content or ''
        # strip_bbappend_sections() left this on disk, and it is what bitbake
        # parsed. Any difference from it invalidates the parsed basehashes.
        if os.path.exists(self._bbappend):
            with open(self._bbappend, 'r') as f:
                parsed_content = f.read()
        else:
            parsed_content = ''

        if not lines:
            if self.MARKER in original_content:
                logger.info(
                    "Removed image debug settings from %s: already provided by the base configuration", self._bbappend)
            image_changed = False
        else:
            new_section = self.MARKER + '\n' + '\n'.join(lines) + '\n'
            new_content = parsed_content
            if new_content and not new_content.endswith('\n'):
                new_content += '\n'
            new_content += new_section

            appends_dir = os.path.dirname(self._bbappend)
            os.makedirs(appends_dir, exist_ok=True)
            with open(self._bbappend, 'w') as f:
                f.write(new_content)
            logger.info("Updated image bbappend %s", self._bbappend)
            image_changed = True

        slirp_changed = self.update_qb_slirp_opt()
        return image_changed or slirp_changed

    def extract_nfs_rootfs(self, nfs_export_base_dir, nfs, target):
        """Refresh the selected rootfs below nfs_export_base_dir."""
        if not self.image_link_name:
            raise DevtoolError(
                'IMAGE_LINK_NAME is empty for %s, --nfs cannot locate the '
                'rootfs tarball without it.' % self.name)
        suffix = '-dbg' if nfs == 'rootfs-dbg' else ''
        rootfs_tarball = os.path.join(
            self.deploy_dir_image, self.image_link_name + suffix + '.tar')
        rootfs_dir = self.nfs_rootfs_dir(nfs_export_base_dir, nfs)
        state_dir = pseudo_state_dir(rootfs_dir)

        if os.path.exists(rootfs_dir):
            logger.warning(
                'Re-extracting %s: files deployed into it are lost and a '
                'target currently booted from it will break.', rootfs_dir)
        for stale_dir in (rootfs_dir, state_dir):
            if os.path.exists(stale_dir):
                shutil.rmtree(stale_dir)

        if not os.path.exists(self.fakerootcmd):
            raise DevtoolError('%s does not exist' % self.fakerootcmd)
        # Reuse the image's own pseudo instead of qemu-helper-native's, so
        # extraction does not depend on a recipe devtool ide-sdk never builds.
        pseudo_cmd = [self.fakerootcmd]
        environment = dict(os.environ)
        for varvalue in (self.fakerootenv or '').split():
            if '=' in varvalue:
                key, value = varvalue.split('=', 1)
                environment[key] = value
        try:
            extract_sdk_rootfs(rootfs_tarball, rootfs_dir, pseudo_cmd, environment)
        except PseudoRootfsError as exc:
            raise DevtoolError('Unable to prepare NFS rootfs: %s' % exc) from exc

        logger.info('NFS rootfs extracted to %s', rootfs_dir)
        helper = self.nfs_runqemu_helper(nfs_export_base_dir, nfs)
        if helper:
            opts = 'slirp' if is_loopback_target(target) else ''
            logger.info(
                'With the build environment sourced, start QEMU with NFS rootfs:\n'
                '  %s %s\n'
                'Pass any additional runqemu options to this helper.',
                helper, opts)

    def update_qb_slirp_opt(self):
        """Update QB_SLIRP_OPT in the image bbappend

        Support connecting to a debugger server running on the target device via
        runqemu's slirp network:
        - If the base value is non-empty (recipe/machine sets QB_SLIRP_OPT):
          only missing port forwards are appended via QB_SLIRP_OPT:append.
        - If the base value is empty (runqemu would use its own built-in default
          of SSH 2222, telnet 2323, tftp): a full QB_SLIRP_OPT assignment is
          written that mirrors that default plus the debugger ports, so that
          runqemu reads the complete set from the .qemuboot.conf.

        Returns True if the bbappend content actually changed, False otherwise.
        """
        ports = sorted({port for cfg in DebuggerCrossConfig._configs.values()
                        for port in list(cfg.debug_server_ports.values()) + cfg.extra_ports})
        if not ports:
            return False

        # Determine which ports are already in the base value
        already = {int(m.group(1))
                   for m in re.finditer(r':(\d+)-:\d+', self.qb_slirp_opt)}
        missing_ports = [p for p in ports if p not in already]
        if not missing_ports:
            logger.info("QB_SLIRP_OPT already contains all needed port forwards")
            return False

        if self.qb_slirp_opt:
            # Base value exists: :append only the missing port forwards
            extra = ''.join(
                ',hostfwd=tcp:127.0.0.1:%d-:%d' % (p, p) for p in missing_ports)
            new_line = 'QB_SLIRP_OPT:append = "%s"' % extra
        else:
            # No base value: mirror runqemu's built-in default (SSH 2222, telnet
            # 2323, tftp) and add the debugger ports.
            all_hostfwds = (
                'hostfwd=tcp:127.0.0.1:2222-:22,'
                'hostfwd=tcp:127.0.0.1:2323-:23'
            )
            all_hostfwds += ''.join(
                ',hostfwd=tcp:127.0.0.1:%d-:%d' % (p, p) for p in missing_ports)
            new_line = 'QB_SLIRP_OPT = "-netdev user,id=net0,%s,tftp=${DEPLOY_DIR_IMAGE}"' % all_hostfwds

        if os.path.exists(self._bbappend):
            with open(self._bbappend, 'r') as f:
                content = f.read()
        else:
            content = ''
        stripped_content = self._strip_marker_section(content, self.QB_SLIRP_MARKER)
        new_content = stripped_content
        if new_content and not new_content.endswith('\n'):
            new_content += '\n'
        new_content += self.QB_SLIRP_MARKER + '\n' + new_line + '\n'

        if new_content == content:
            logger.debug("QB_SLIRP_OPT in %s is already up to date", self._bbappend)
            return False

        appends_dir = os.path.dirname(self._bbappend)
        os.makedirs(appends_dir, exist_ok=True)
        with open(self._bbappend, 'w') as f:
            f.write(new_content)
        logger.info("Updated QB_SLIRP_OPT in %s: %s", self._bbappend, new_line)
        return True


class RecipeMetaIdeSupport:
    """For the shared sysroots mode meta-ide-support is needed

    For use cases where just a cross tool-chain is required but
    no recipe is used, devtool ide-sdk abstracts calling bitbake meta-ide-support
    and bitbake build-sysroots. This also allows to expose the cross-toolchains
    to IDEs. For example VSCode support different tool-chains with e.g. cmake-kits.
    """

    def __init__(self):
        self.bootstrap_tasks = ['meta-ide-support:do_build']
        self.topdir = None
        self.datadir = None
        self.deploy_dir_image = None
        self.build_sys = None
        # From toolchain-scripts
        self.real_multimach_target_sys = None

    def initialize(self, config, tinfoil):
        meta_ide_support_d = parse_recipe(
            config, tinfoil, 'meta-ide-support', appends=True, filter_workspace=False)
        if not meta_ide_support_d:
            raise DevtoolError("Parsing meta-ide-support recipe failed")

        self.topdir = meta_ide_support_d.getVar('TOPDIR')
        self.datadir = meta_ide_support_d.getVar('datadir')
        self.deploy_dir_image = meta_ide_support_d.getVar(
            'DEPLOY_DIR_IMAGE')
        self.build_sys = meta_ide_support_d.getVar('BUILD_SYS')
        self.real_multimach_target_sys = meta_ide_support_d.getVar(
            'REAL_MULTIMACH_TARGET_SYS')


class RecipeBuildSysroots:
    """For the shared sysroots mode build-sysroots is needed"""

    def __init__(self):
        self.standalone_sysroot = None
        self.standalone_sysroot_native = None
        self.bootstrap_tasks = [
            'build-sysroots:do_build_target_sysroot',
            'build-sysroots:do_build_native_sysroot'
        ]

    def initialize(self, config, tinfoil):
        build_sysroots_d = parse_recipe(
            config, tinfoil, 'build-sysroots', appends=True, filter_workspace=False)
        if not build_sysroots_d:
            raise DevtoolError("Parsing build-sysroots recipe failed")
        self.standalone_sysroot = build_sysroots_d.getVar(
            'STANDALONE_SYSROOT')
        self.standalone_sysroot_native = build_sysroots_d.getVar(
            'STANDALONE_SYSROOT_NATIVE')


class SharedSysrootsEnv:
    """Handle the shared sysroots based workflow

    Support the workflow with just a tool-chain without a recipe.
    It's basically like:
      bitbake some-dependencies
      bitbake meta-ide-support
      bitbake build-sysroots
      Use the environment-* file found in the deploy folder
    """

    def __init__(self):
        self.ide_support = None
        self.build_sysroots = None

    def initialize(self, ide_support, build_sysroots):
        self.ide_support = ide_support
        self.build_sysroots = build_sysroots

    def setup_ide(self, ide):
        ide.setup(self)


class RecipeNotModified:
    """Handling of recipes added to the Direct DSK shared sysroots."""

    def __init__(self, name):
        self.name = name
        self.bootstrap_tasks = [name + ':do_populate_sysroot']

class ExecutableBinary:
    """Represent an installed executable binary of a modified recipe"""

    def __init__(self, image_dir_d, binary_path,
                 systemd_services, init_scripts):
        self.image_dir_d = image_dir_d
        self.binary_path = binary_path
        self.init_script = None
        self.systemd_service = None

        self._init_service_for_binary(systemd_services)
        self._init_init_script_for_binary(init_scripts)

    def _init_service_for_binary(self, systemd_services):
        """Find systemd service file that handles this binary"""
        service_dirs = [
            'etc/systemd/system',
            'lib/systemd/system',
            'usr/lib/systemd/system'
        ]
        for _, services in systemd_services.items():
            for service in services:
                for service_dir in service_dirs:
                    service_path = os.path.join(self.image_dir_d, service_dir, service)
                    if os.path.exists(service_path):
                        try:
                            with open(service_path, 'r') as f:
                                for line in f:
                                    if line.strip().startswith('ExecStart='):
                                        exec_start = line.strip()[10:].strip()  # Remove 'ExecStart='
                                        # Remove any leading modifiers like '-' or '@'
                                        exec_start = exec_start.lstrip('-@')
                                        # Get the first word (the executable path)
                                        exec_binary = exec_start.split()[0] if exec_start.split() else ''
                                        if exec_binary == self.binary_path or exec_binary.endswith('/' + self.binary_path.lstrip('/')):
                                            logger.debug("Found systemd service for binary %s: %s" % (self.binary_path, service))
                                            self.systemd_service = service
                        except (IOError, OSError):
                            continue

    def _init_init_script_for_binary(self, init_scripts):
        """Find SysV init script that handles this binary"""
        init_dirs = [
            'etc/init.d',
            'etc/rc.d/init.d'
        ]
        for _, init_scripts in init_scripts.items():
            for init_script in init_scripts:
                for init_dir in init_dirs:
                    init_path = os.path.join(self.image_dir_d, init_dir, init_script)
                    if os.path.exists(init_path):
                        init_script_path = os.path.join("/", init_dir, init_script)
                        binary_name = os.path.basename(self.binary_path)
                        # if the init script file name is equal to the binary file name, return it directly
                        if os.path.basename(init_script) == binary_name:
                            logger.debug("Found SysV init script for binary %s: %s" % (self.binary_path, init_script_path))
                            self.init_script = init_script_path
                        # Otherwise check if the script containes a reference to the binary
                        try:
                            with open(init_path, 'r') as f:
                                content = f.read()
                                pattern = r'\b' + re.escape(binary_name) + r'\b'
                                if re.search(pattern, content):
                                    logger.debug("Found SysV init script for binary %s: %s" % (self.binary_path, init_script_path))
                                    self.init_script = init_script_path
                                    return
                        except (IOError, OSError):
                            continue

    @property
    def binary_host_path(self):
        """Get the absolute path of this binary on the host"""
        return os.path.join(self.image_dir_d, self.binary_path.lstrip('/'))

    @property
    def runs_as_service(self):
        """Check if this binary is run by a service or init script"""
        return self.systemd_service is not None or self.init_script is not None

    @property
    def start_command(self):
        """Get the command to start this binary"""
        if self.systemd_service:
            return "systemctl start %s" % self.systemd_service
        if self.init_script:
            return "%s start" % self.init_script
        return None

    @property
    def stop_command(self):
        """Get the command to stop this binary"""
        if self.systemd_service:
            return "systemctl stop %s" % self.systemd_service
        if self.init_script:
            return "%s stop" % self.init_script
        return None

    @property
    def pid_command(self):
        """Get the command to get the PID of this binary"""
        if self.systemd_service:
            return "systemctl show --property MainPID --value %s" % self.systemd_service
        if self.init_script:
            return "pidof %s" % os.path.basename(self.binary_path)
        return None


class RecipeModified:
    """Handling of recipes in the workspace created by devtool modify"""
    OE_INIT_BUILD_ENV = 'oe-init-build-env'
    INIT_BUILD_ENV = 'init-build-env'

    VALID_BASH_ENV_NAME_CHARS = re.compile(r"^[a-zA-Z0-9_]*$")

    MARKER = '# devtool ide-sdk: clangd toolchain support'

    def __init__(self, name, orig_bbappend_content=None):
        self.name = name
        self.bootstrap_tasks = [name + ':do_install']
        self._orig_bbappend_content = orig_bbappend_content
        self.debugger_cross = None
        # workspace
        self.real_srctree = None
        self.srctree = None
        self.ide_sdk_dir = None
        self.ide_sdk_scripts_dir = None
        self.bbappend = None
        # recipe variables from d.getVar
        self.b = None
        self.base_libdir = None
        self.bblayers = None
        self.bindir = None
        self.bitbakepath = None
        self.bpn = None
        self.d = None
        self.debug_build = None
        self.reverse_debug_prefix_map = {}
        self.fakerootcmd = None
        self.fakerootenv = None
        self.libdir = None
        self.max_process = None
        self.package_arch = None
        self.package_debug_split_style = None
        self.path = None
        self.pn = None
        self.recipe_id = None
        self.recipe_sysroot = None
        self.recipe_sysroot_native = None
        self.s = None
        self.staging_incdir = None
        self.strip_cmd = None
        self.target_arch = None
        self.tmpdir = None
        self.toolchain = None
        self.ide_sdk_intellisense = None
        self.topdir = None
        self.workdir = None
        # Maps each package name (from PACKAGES) to the glob patterns from its FILES variable
        self.packages_files = {}
        # Service management
        self.systemd_services = {}
        self.init_scripts = {}
        # replicate bitbake build environment
        self.exported_vars = None
        self.cmd_compile = None
        self.__oe_init_dir = None
        # main build tool used by this recipe
        self.build_tool = BuildTool.UNDEFINED
        # Whether this recipe benefits from gdbserver and rootfs-dbg in the image.
        self.wants_gdbserver = True
        # Whether to warn when DEBUG_BUILD is not set.  Kernel modules are built
        # by the kernel's build system and DEBUG_BUILD does not influence them.
        self.wants_debug_build = True
        # Whether this recipe provides a ptest package
        self.has_ptest = False
        # build_tool = cmake
        self.oecmake_generator = None
        self.cmake_cache_vars = None
        # build_tool = meson
        self.meson_buildtype = None
        self.meson_wrapper = None
        self.mesonopts = None
        self.extra_oemeson = None
        self.meson_cross_file = None
        # kernel module
        self.make_targets = None
        self.extra_oemake = None
        self.kernel_cc = None
        self.staging_kernel_dir = None

        # Populated after bitbake built all the recipes
        self._installed_binaries = None
        self._gdb_pretty_print_scripts = None

    @staticmethod
    def _strip_marker_section(content, marker):
        """Remove one devtool ide-sdk marker section, if present"""
        return re.sub(
            r'^' + re.escape(marker) + r'\n(?:[^\n]+\n)*',
            '', content, flags=re.MULTILINE)

    @classmethod
    def strip_bbappend_sections(cls, config, recipe_names):
        """Remove devtool ide-sdk's own bbappend section from earlier runs

        Runs before setup_tinfoil() so that every parse in this session sees the
        original recipe content and only non standard flags can be bbappended later.
        """
        originals = {}
        appends_dir = os.path.join(config.workspace_path, 'appends')
        for name in recipe_names:
            bbappend = os.path.join(appends_dir, name + '.bbappend')
            if not os.path.exists(bbappend):
                continue
            with open(bbappend, 'r') as f:
                content = f.read()
            originals[name] = content
            stripped = cls._strip_marker_section(content, cls.MARKER)
            if stripped != content:
                with open(bbappend, 'w') as f:
                    f.write(stripped)
        return originals

    def update_bbappend(self):
        """Add DEPENDS on clang-native when clangd is used as only the IntelliSense engine"""
        wants_clang_native = (
            self.build_tool.is_c_cpp
            and self.ide_sdk_intellisense == 'clangd'
            and self.toolchain != 'clang')

        original_content = self._orig_bbappend_content or ''
        if os.path.exists(self.bbappend):
            with open(self.bbappend, 'r') as f:
                parsed_content = f.read()
        else:
            parsed_content = ''

        if not wants_clang_native:
            if self.MARKER in original_content:
                logger.info(
                    "Removed clangd toolchain support from %s: no longer needed", self.bbappend)
            return False

        new_section = self.MARKER + '\nDEPENDS:append = " clang-native"\n'
        new_content = parsed_content
        if new_content and not new_content.endswith('\n'):
            new_content += '\n'
        new_content += new_section

        if new_content == original_content:
            return False

        appends_dir = os.path.dirname(self.bbappend)
        os.makedirs(appends_dir, exist_ok=True)
        with open(self.bbappend, 'w') as f:
            f.write(new_content)
        logger.info(
            "Updated %s: added DEPENDS on clang-native, needed to stage clangd "
            "for IDE_SDK_INTELLISENSE=\"clangd\"", self.bbappend)
        return True

    def initialize(self, config, workspace, tinfoil):
        recipe_d = parse_recipe(
            config, tinfoil, self.name, appends=True, filter_workspace=False)
        if not recipe_d:
            raise DevtoolError("Parsing %s recipe failed" % self.name)

        # Verify this recipe is built as externalsrc setup by devtool modify
        workspacepn = check_workspace_recipe(
            workspace, self.name, bbclassextend=True)
        self.srctree = workspace[workspacepn]['srctree']
        # Need to grab this here in case the source is within a subdirectory
        self.real_srctree = get_real_srctree(
            self.srctree, recipe_d.getVar('S'), recipe_d.getVar('UNPACKDIR'))
        self.bbappend = workspace[workspacepn]['bbappend']

        self.ide_sdk_dir = os.path.join(
            config.workspace_path, 'ide-sdk', self.name)
        if os.path.exists(self.ide_sdk_dir):
            shutil.rmtree(self.ide_sdk_dir)
        self.ide_sdk_scripts_dir = os.path.join(self.ide_sdk_dir, 'scripts')

        self.b = recipe_d.getVar('B')
        self.base_libdir = recipe_d.getVar('base_libdir')
        self.bblayers = recipe_d.getVar('BBLAYERS').split()
        self.bindir = recipe_d.getVar('bindir')
        self.bitbakepath = recipe_d.getVar('BITBAKEPATH')
        self.bpn = recipe_d.getVar('BPN')
        self.cc = recipe_d.getVar('CC')
        self.cxx = recipe_d.getVar('CXX')
        self.d = recipe_d.getVar('D')
        self.debug_build = recipe_d.getVar('DEBUG_BUILD')
        self.fakerootcmd = recipe_d.getVar('FAKEROOTCMD')
        self.fakerootenv = recipe_d.getVar('FAKEROOTENV')
        self.libdir = recipe_d.getVar('libdir')
        self.max_process = int(recipe_d.getVar(
            "BB_NUMBER_THREADS") or os.cpu_count() or 1)
        self.package_arch = recipe_d.getVar('PACKAGE_ARCH')
        self.package_debug_split_style = recipe_d.getVar(
            'PACKAGE_DEBUG_SPLIT_STYLE')
        for package in (recipe_d.getVar('PACKAGES') or '').split():
            self.packages_files[package] = recipe_d.getVar('FILES:' + package) or ''
        self.path = recipe_d.getVar('PATH')
        self.pn = recipe_d.getVar('PN')
        self.recipe_sysroot = os.path.realpath(
            recipe_d.getVar('RECIPE_SYSROOT'))
        self.recipe_sysroot_native = os.path.realpath(
            recipe_d.getVar('RECIPE_SYSROOT_NATIVE'))
        self.s = recipe_d.getVar('S')
        self.staging_bindir_toolchain = os.path.realpath(
            recipe_d.getVar('STAGING_BINDIR_TOOLCHAIN'))
        self.staging_incdir = os.path.realpath(
            recipe_d.getVar('STAGING_INCDIR'))
        self.strip_cmd = recipe_d.getVar('STRIP')
        self.target_arch = recipe_d.getVar('TARGET_ARCH')
        self.tmpdir = os.path.realpath(recipe_d.getVar('TMPDIR'))
        self.toolchain = recipe_d.getVar('TOOLCHAIN')
        self.ide_sdk_intellisense = recipe_d.getVar('IDE_SDK_INTELLISENSE') or (
            'clangd' if self.toolchain == 'clang' else 'cpptools')
        self.topdir = recipe_d.getVar('TOPDIR')
        self.workdir = os.path.realpath(recipe_d.getVar('WORKDIR'))

        self.__init_exported_variables(recipe_d)
        self.__init_systemd_services(recipe_d)
        self.__init_init_scripts(recipe_d)

        if bb.data.inherits_class('cmake', recipe_d):
            self.oecmake_generator = recipe_d.getVar('OECMAKE_GENERATOR')
            self.__init_cmake_preset_cache(recipe_d)
            self.build_tool = BuildTool.CMAKE
        elif bb.data.inherits_class('meson', recipe_d):
            self.meson_buildtype = recipe_d.getVar('MESON_BUILDTYPE')
            self.mesonopts = recipe_d.getVar('MESONOPTS')
            self.extra_oemeson = recipe_d.getVar('EXTRA_OEMESON')
            self.meson_cross_file = recipe_d.getVar('MESON_CROSS_FILE')
            self.build_tool = BuildTool.MESON
        elif bb.data.inherits_class('module', recipe_d):
            self.build_tool = BuildTool.KERNEL_MODULE
            self.wants_gdbserver = False
            self.wants_debug_build = False
            make_targets = recipe_d.getVar('MAKE_TARGETS')
            if make_targets:
                self.make_targets = shlex.split(make_targets)
            else:
                self.make_targets = ["all"]
            extra_oemake = recipe_d.getVar('EXTRA_OEMAKE')
            if extra_oemake:
                self.extra_oemake = shlex.split(extra_oemake)
            else:
                self.extra_oemake = []
            self.kernel_cc = recipe_d.getVar('KERNEL_CC')
            self.staging_kernel_dir = recipe_d.getVar('STAGING_KERNEL_DIR')
            # Export up the environment for building kernel modules
            kernel_module_os_env(recipe_d, self.exported_vars)

        # For the kernel the KERNEL_CC variable contains the prefix-map arguments
        if self.build_tool is BuildTool.KERNEL_MODULE:
            self.reverse_debug_prefix_map = self._init_reverse_debug_prefix_map(
                self.kernel_cc)
        else:
            self.reverse_debug_prefix_map = self._init_reverse_debug_prefix_map(
                recipe_d.getVar('DEBUG_PREFIX_MAP'))

        self.has_ptest = bb.data.inherits_class('ptest', recipe_d)

        # Recipe ID is the identifier for IDE config sections
        self.recipe_id = self.bpn + "-" + self.package_arch
        self.recipe_id_pretty = self.bpn + ": " + self.package_arch

    @staticmethod
    def is_valid_shell_variable(var):
        """Skip strange shell variables like systemd

        prevent from strange bugs because of strange variables which
        are not used in this context but break various tools.
        """
        if RecipeModified.VALID_BASH_ENV_NAME_CHARS.match(var):
            bb.debug(1, "ignoring variable: %s" % var)
            return True
        return False

    @staticmethod
    def _find_elf_dirs(root):
        """Return every directory under root that contains an ELF file"""
        elf_dirs = []
        for dirpath, _dirnames, filenames in os.walk(root):
            for filename in filenames:
                file_path = os.path.join(dirpath, filename)
                if os.path.islink(file_path):
                    continue
                try:
                    with open(file_path, 'rb') as elf_file:
                        is_elf = elf_file.read(4) == b'\x7fELF'
                except OSError:
                    continue
                if is_elf:
                    elf_dirs.append(dirpath)
                    break
        return elf_dirs

    def solib_search_path(self, image):
        """Search for debug symbols

        This recipe's own D is searched (for ELF files, wherever they are
        installed), so that this recipe's freshly rebuilt libraries take
        precedence over any stale copy from the last full image build.
        "set sysroot D" alone cannot achieve this: GDB's sysroot (tested with
        GDB 17.2) only matches "/lib" paths, but D usually only contains
        "usr/lib" (no top-level "lib" symlink), so it falls back to
        solib-search-path's basename matching instead. This also covers
        libraries not installed into the standard libdirs.

        D is listed first, ahead of rootfs-dbg/rootfs. For every other
        library, i.e. one provided by another package, debug symbols are
        grabbed from the -dbg packages in rootfs-dbg. gdb, perf, and
        systemtap need to find the executable/library itself first, and
        through its debuglink find the symbols file, so the plain rootfs
        paths are added too.
        """
        base_libdir = self.base_libdir.lstrip('/')
        libdir = self.libdir.lstrip('/')
        # dedupe (e.g. base_libdir = libdir) while keeping the order (using dict instead of set)
        so_paths = dict.fromkeys([
            # This recipe's own rebuilt libraries/plugins, ahead of rootfs-dbg/rootfs.
            *self._find_elf_dirs(self.d),
            # debug symbols for package_debug_split_style: debug-with-srcpkg or .debug
            os.path.join(image.rootfs_dbg, base_libdir, ".debug"),
            os.path.join(image.rootfs_dbg, libdir, ".debug"),
            # debug symbols for package_debug_split_style: debug-file-directory
            os.path.join(image.rootfs_dbg, "usr", "lib", "debug"),

            # The binaries are required as well, the debug packages are not enough
            # With image-combined-dbg.bbclass the binaries are copied into rootfs-dbg
            os.path.join(image.rootfs_dbg, base_libdir),
            os.path.join(image.rootfs_dbg, libdir),
            # Without image-combined-dbg.bbclass the binaries are only in rootfs
            # (stepping into rootfs-dbg sources needs image-combined-dbg.bbclass)
            os.path.join(image.rootfs, base_libdir),
            os.path.join(image.rootfs, libdir)
        ])
        return list(so_paths)

    def solib_search_path_str(self, image):
        """Return a : separated list of paths usable by GDB's set solib-search-path"""
        return ':'.join(self.solib_search_path(image))

    def _init_reverse_debug_prefix_map(self, debug_prefix_map):
        """Parses GCC map options and returns a mapping of target to host paths.

        This function scans a string containing GCC options such as -fdebug-prefix-map,
        -fmacro-prefix-map, and -ffile-prefix-map (in both '--option value' and '--option=value'
        forms), extracting all mappings from target paths (used in debug info) to host source
        paths. If multiple mappings for the same target path are found, the most suitable
        host path is selected (preferring 'sources' over 'build' directories).
        """
        prefixes = ("-fdebug-prefix-map", "-fmacro-prefix-map", "-ffile-prefix-map")
        all_mappings = {}
        args = shlex.split(debug_prefix_map)
        i = 0

        # Collect all mappings, storing potentially multiple host paths per target path
        while i < len(args):
            arg = args[i]
            mapping = None
            for prefix in prefixes:
                if arg == prefix:
                    i += 1
                    mapping = args[i]
                    break
                elif arg.startswith(prefix + '='):
                    mapping = arg[len(prefix)+1:]
                    break
            if mapping:
                host_path, target_path = mapping.split('=', 1)
                if target_path:
                    if target_path not in all_mappings:
                        all_mappings[target_path] = []
                    all_mappings[target_path].append(os.path.realpath(host_path))
            i += 1

        # Select the best host path for each target path (only 1:1 mappings are supported by GDB)
        mappings = {}
        unused_host_paths = []
        for target_path, host_paths in all_mappings.items():
            if len(host_paths) == 1:
                mappings[target_path] = host_paths[0]
            else:
                # First priority path for sources is the source directory S
                # Second priority path is any other directory
                # Least priority is the build directory B, which probably contains only generated source files
                sources_paths = [path for path in host_paths if os.path.realpath(path).startswith(os.path.realpath(self.s))]
                if sources_paths:
                    mappings[target_path] = sources_paths[0]
                    unused_host_paths.extend([path for path in host_paths if path != sources_paths[0]])
                else:
                    # If no 'sources' path, prefer non-'build' paths
                    non_build_paths = [path for path in host_paths if not os.path.realpath(path).startswith(os.path.realpath(self.b))]
                    if non_build_paths:
                        mappings[target_path] = non_build_paths[0]
                        unused_host_paths.extend([path for path in host_paths if path != non_build_paths[0]])
                    else:
                        # Fall back to first path if all are build paths
                        mappings[target_path] = host_paths[0]
                        unused_host_paths.extend(host_paths[1:])

        if unused_host_paths:
            logger.info("Some source directories mapped by -fdebug-prefix-map are not included in the debugger search paths. Ignored host paths: %s", unused_host_paths)

        self._add_broken_srctree_prefix_map(mappings)

        return mappings

    def _add_broken_srctree_prefix_map(self, mappings):
        """Work around a -f*-prefix-map / DWARF path resolution issue affecting
        out-of-tree devtool workspaces (e.g. meson recipes built via 'devtool modify'
        with the clang toolchain).

        meson/ninja may invoke the compiler with a *relative* source file path
        when the build directory B (under WORKDIR) and the source directory S
        (relocated outside WORKDIR by 'devtool modify') only share a distant
        common ancestor. -fdebug-prefix-map/-ffile-prefix-map only rewrite
        paths that literally start with the mapped host prefix, so a relative
        path argument is never rewritten: only DW_AT_comp_dir (which is
        absolute) gets rewritten, DW_AT_name stays relative and unrewritten.

        This has only been observed to actually happen with the clang
        toolchain: clang's meson/ninja invocation embeds a relative DW_AT_name
        for out-of-tree sources, while gcc, even via meson/ninja, embeds an
        absolute (and correctly -fdebug-prefix-map-rewritten) DW_AT_name, so
        no underflow can happen there - confirmed empirically:
        oe-selftest's test_devtool_ide_sdk_none_qemu (gcc toolchain, covering
        both cmake-example and meson-example) fails when this workaround is
        applied unconditionally to meson, while the dedicated clang tests
        (test_devtool_ide_sdk_{code,none}_meson_clang) require it. cmake
        (with the Ninja or Makefiles generators used here) always passes
        absolute source paths to the compiler regardless of toolchain, so it
        never needs this workaround either. Applying this workaround outside
        of the meson+clang combination would incorrectly discard the correct
        (and, for gcc/cmake, already working) comp_dir-based mapping - see the
        'del mappings[target_path]' below - falling back to the generic
        '/usr/src/debug' mapping to the image's (stale, whole-image-build-time)
        rootfs-dbg instead of the live source tree.

        Debuggers resolve the compile unit path by joining DW_AT_comp_dir with
        the relative DW_AT_name, popping one path component per leading "..".
        If DW_AT_name contains more ".." components than DW_AT_comp_dir has
        path components, the extra ".." are no-ops once the root is reached
        (they can't go above "/"), so the final resolved path becomes "/"
        followed by the leftover (non-"..") components of DW_AT_name - i.e. a
        suffix of the real, absolute source directory rather than the
        "/usr/src/debug/<pn>/<pv>" prefix that DEBUG_PREFIX_MAP and the
        generated sourceMap/sourceFileMap assume.

        This computes that resolved suffix for the recipe's own source
        directory (S) and replaces the (now dead, since every file under S is
        affected the same way) comp_dir-based mapping with it, so debuggers
        relying on prefix matching (e.g. CodeLLDB, GDB) can still locate the
        sources.

        Note: the original comp_dir-based target_path is removed rather than
        kept alongside the new one. Keeping both would mean two different
        target paths map to the same host path (S), which is ambiguous when a
        debugger needs to go the other way round: translating a local file
        (opened from the host/workspace) back into a debug-info path in order
        to resolve a source breakpoint. CodeLLDB in particular appears to
        pick the first-registered ("normal", comp_dir-based) mapping in that
        case, which never matches any real compile unit here, leaving the
        breakpoint pending with 0 locations.
        """
        if self.build_tool is not BuildTool.MESON or self.toolchain != "clang":
            return
        if not self.real_srctree or not self.b:
            return

        b_real = os.path.realpath(self.b)
        srctree_real = os.path.realpath(self.real_srctree)
        common = os.path.commonpath([b_real, srctree_real])
        if common in (b_real, srctree_real):
            # B is srctree (or a parent of it), or B is nested inside srctree:
            # either way the compiler is never invoked with a source path that
            # climbs above the common ancestor, so no underflow can happen.
            return

        # Number of ".." path components needed to get from the compiler's
        # working directory (the build directory B) up to the common ancestor
        # with the source tree. This is how many leading ".." components
        # DW_AT_name would contain for sources directly under S.
        overshoot_components = len(os.path.relpath(b_real, common).split(os.sep))

        for target_path, host_path in list(mappings.items()):
            if host_path != srctree_real:
                # Only the recipe's own source directory (S) is relocated by
                # devtool modify, other mapped directories are unaffected.
                continue
            comp_dir_components = len([c for c in target_path.split('/') if c])
            if overshoot_components <= comp_dir_components:
                # The rewritten DW_AT_comp_dir has enough components to
                # absorb all the ".." in DW_AT_name, no underflow happens.
                continue
            broken_target = '/' + os.path.relpath(srctree_real, common)
            if broken_target not in mappings:
                mappings[broken_target] = host_path
            # The comp_dir-based target_path never actually occurs in the
            # debug info for files under S (all of them hit the same
            # overshoot), so keeping it around only creates an ambiguous
            # reverse mapping (see docstring above). Drop it.
            del mappings[target_path]

    @property
    def gdb_pretty_print_scripts(self):
        if self._gdb_pretty_print_scripts is None:
            if self.toolchain == "gcc":
                gcc_python_helpers_pattern = os.path.join(self.recipe_sysroot, "usr", "share", "gcc-*", "python")
                gcc_python_helpers_dirs = glob.glob(gcc_python_helpers_pattern)
                if len(gcc_python_helpers_dirs) > 1:
                    raise DevtoolError(
                        "Found multiple gcc python helpers directories matching %s: %s. "
                        "The recipe sysroot likely has stale files from a previous gcc version, "
                        "remove tmp/work and rebuild the recipe." % (gcc_python_helpers_pattern, gcc_python_helpers_dirs))
                if gcc_python_helpers_dirs:
                    gcc_python_helpers = gcc_python_helpers_dirs[0]
                else:
                    logger.warning("Could not find gcc python helpers directory matching: %s", gcc_python_helpers_pattern)
                    gcc_python_helpers = ""
                pretty_print_scripts = [
                    "import sys",
                    "sys.path.insert(0, '" + gcc_python_helpers + "')",
                    "from libstdcxx.v6.printers import register_libstdcxx_printers",
                    "register_libstdcxx_printers(None)"
                ]
                self._gdb_pretty_print_scripts = pretty_print_scripts
            else:
                self._gdb_pretty_print_scripts = ""
        return self._gdb_pretty_print_scripts

    def __init_exported_variables(self, d):
        """Find all variables with export flag set.

        This allows to generate IDE configurations which compile with the same
        environment as bitbake does. That's at least a reasonable default behavior.
        """
        exported_vars = {}

        vars = (key for key in d.keys() if not key.startswith(
            "__") and not d.getVarFlag(key, "func", False))
        for var in sorted(vars):
            func = d.getVarFlag(var, "func", False)
            if d.getVarFlag(var, 'python', False) and func:
                continue
            export = d.getVarFlag(var, "export", False)
            unexport = d.getVarFlag(var, "unexport", False)
            if not export and not unexport and not func:
                continue
            if unexport:
                continue

            val = d.getVar(var)
            if val is None:
                continue
            if set(var) & set("-.{}+"):
                logger.warn(
                    "Warning: Found invalid character in variable name %s", str(var))
                continue
            varExpanded = d.expand(var)
            val = str(val)

            if not RecipeModified.is_valid_shell_variable(varExpanded):
                continue

            if func:
                code_line = "line: {0}, file: {1}\n".format(
                    d.getVarFlag(var, "lineno", False),
                    d.getVarFlag(var, "filename", False))
                val = val.rstrip('\n')
                logger.warn("Warning: exported shell function %s() is not exported (%s)" %
                            (varExpanded, code_line))
                continue

            if export:
                exported_vars[varExpanded] = val.strip()
                continue

        self.exported_vars = exported_vars

    def __init_systemd_services(self, d):
        """Find all systemd service files for the recipe."""
        services = {}
        if bb.data.inherits_class('systemd', d):
            systemd_packages = d.getVar('SYSTEMD_PACKAGES')
            if systemd_packages:
                for package in systemd_packages.split():
                    services[package] = d.getVar('SYSTEMD_SERVICE:' + package).split()
        self.systemd_services = services

    def __init_init_scripts(self, d):
        """Find all SysV init scripts for the recipe."""
        init_scripts = {}
        if bb.data.inherits_class('update-rc.d', d):
            script_packages = d.getVar('INITSCRIPT_PACKAGES')
            if script_packages:
                for package in script_packages.split():
                    initscript_name = d.getVar('INITSCRIPT_NAME:' + package)
                    if initscript_name:
                        # Handle both single script and multiple scripts
                        scripts = initscript_name.split()
                        if scripts:
                            init_scripts[package] = scripts
            else:
                # If INITSCRIPT_PACKAGES is not set, check for default INITSCRIPT_NAME
                initscript_name = d.getVar('INITSCRIPT_NAME')
                if initscript_name:
                    scripts = initscript_name.split()
                    if scripts:
                        # Use PN as the default package name when INITSCRIPT_PACKAGES is not set
                        pn = d.getVar('PN')
                        if pn:
                            init_scripts[pn] = scripts
        self.init_scripts = init_scripts

    def __init_cmake_preset_cache(self, d):
        """Get the arguments passed to cmake

        Replicate the cmake configure arguments with all details to
        share on build folder between bitbake and SDK.
        """
        site_file = os.path.join(self.workdir, 'site-file.cmake')
        if os.path.exists(site_file):
            print("Warning: site-file.cmake is not supported")

        cache_vars = {}
        oecmake_args = d.getVar('OECMAKE_ARGS').split()
        extra_oecmake = d.getVar('EXTRA_OECMAKE').split()
        for param in sorted(oecmake_args + extra_oecmake):
            d_pref = "-D"
            if param.startswith(d_pref):
                param = param[len(d_pref):]
            else:
                print("Error: expected a -D")
            param_s = param.split('=', 1)
            param_nt = param_s[0].split(':', 1)

            def handle_undefined_variable(var):
                if var.startswith('${') and var.endswith('}'):
                    return ''
                else:
                    return var
            # Example: FOO=ON
            if len(param_nt) == 1:
                cache_vars[param_s[0]] = handle_undefined_variable(param_s[1])
            # Example: FOO:PATH=/tmp
            elif len(param_nt) == 2:
                cache_vars[param_nt[0]] = {
                    "type": param_nt[1],
                    "value": handle_undefined_variable(param_s[1]),
                }
            else:
                print("Error: cannot parse %s" % param)
        self.cmake_cache_vars = cache_vars

    def cmake_preset(self):
        """Create a preset for cmake that mimics how bitbake calls cmake"""
        toolchain_file = os.path.join(self.workdir, 'toolchain.cmake')
        cmake_executable = os.path.join(
            self.recipe_sysroot_native, 'usr', 'bin', 'cmake')
        self.cmd_compile = cmake_executable + " --build --preset " + self.recipe_id

        preset_dict_configure = {
            "name": self.recipe_id,
            "displayName": self.recipe_id_pretty,
            "description": "Bitbake build environment for the recipe %s compiled for %s" % (self.bpn, self.package_arch),
            "binaryDir": self.b,
            "generator": self.oecmake_generator,
            "toolchainFile": toolchain_file,
            "cacheVariables": self.cmake_cache_vars,
            "environment": self.exported_vars,
            "cmakeExecutable": cmake_executable
        }

        preset_dict_build = {
            "name": self.recipe_id,
            "displayName": self.recipe_id_pretty,
            "description": "Bitbake build environment for the recipe %s compiled for %s" % (self.bpn, self.package_arch),
            "configurePreset": self.recipe_id,
            "inheritConfigureEnvironment": True
        }

        preset_dict_test = {
            "name": self.recipe_id,
            "displayName": self.recipe_id_pretty,
            "description": "Bitbake build environment for the recipe %s compiled for %s" % (self.bpn, self.package_arch),
            "configurePreset": self.recipe_id,
            "inheritConfigureEnvironment": True
        }

        preset_dict = {
            "version": 3,  # cmake 3.21, backward compatible with kirkstone
            "configurePresets": [preset_dict_configure],
            "buildPresets": [preset_dict_build],
            "testPresets": [preset_dict_test]
        }

        # Finally write the json file
        json_file = 'CMakeUserPresets.json'
        json_path = os.path.join(self.real_srctree, json_file)
        logger.info("Updating CMake preset: %s (%s)" % (json_file, json_path))
        if not os.path.exists(self.real_srctree):
            os.makedirs(self.real_srctree)
        try:
            with open(json_path) as f:
                orig_dict = json.load(f)
        except json.decoder.JSONDecodeError:
            logger.info(
                "Decoding %s failed. Probably because of comments in the json file" % json_path)
            orig_dict = {}
        except FileNotFoundError:
            orig_dict = {}

        # Add or update the presets for the recipe and keep other presets
        for k, v in preset_dict.items():
            if isinstance(v, list):
                update_preset = v[0]
                preset_added = False
                if k in orig_dict:
                    for index, orig_preset in enumerate(orig_dict[k]):
                        if 'name' in orig_preset:
                            if orig_preset['name'] == update_preset['name']:
                                logger.debug("Updating preset: %s" %
                                             orig_preset['name'])
                                orig_dict[k][index] = update_preset
                                preset_added = True
                                break
                            else:
                                logger.debug("keeping preset: %s" %
                                             orig_preset['name'])
                        else:
                            logger.warn("preset without a name found")
                if not preset_added:
                    if not k in orig_dict:
                        orig_dict[k] = []
                    orig_dict[k].append(update_preset)
                    logger.debug("Added preset: %s" %
                                 update_preset['name'])
            else:
                orig_dict[k] = v

        with open(json_path, 'w') as f:
            json.dump(orig_dict, f, indent=4)

    def gen_meson_wrapper(self):
        """Generate a wrapper script to call meson with the cross environment"""
        bb.utils.mkdirhier(self.ide_sdk_scripts_dir)
        meson_wrapper = os.path.join(self.ide_sdk_scripts_dir, 'meson')
        meson_real = os.path.join(
            self.recipe_sysroot_native, 'usr', 'bin', 'meson.real')
        with open(meson_wrapper, 'w') as mwrap:
            mwrap.write("#!/bin/sh" + os.linesep)
            for var, val in self.exported_vars.items():
                mwrap.write('export %s="%s"' % (var, val) + os.linesep)
            mwrap.write("unset CC CXX CPP LD AR NM STRIP" + os.linesep)
            private_temp = os.path.join(self.b, "meson-private", "tmp")
            mwrap.write('mkdir -p "%s"' % private_temp + os.linesep)
            mwrap.write('export TMPDIR="%s"' % private_temp + os.linesep)
            mwrap.write('exec "%s" "$@"' % meson_real + os.linesep)
        st = os.stat(meson_wrapper)
        os.chmod(meson_wrapper, st.st_mode | stat.S_IEXEC)
        self.meson_wrapper = meson_wrapper
        self.cmd_compile = meson_wrapper + " compile -C " + self.b

    def which(self, executable):
        bin_path = shutil.which(executable, path=self.path)
        if not bin_path:
            raise DevtoolError(
                'Cannot find %s. Probably the recipe %s is not built yet.' % (executable, self.bpn))
        return bin_path

    @staticmethod
    def is_elf_file(file_path):
        with open(file_path, "rb") as f:
            data = f.read(4)
        if data == b'\x7fELF':
            return True
        return False

    @property
    def installed_binaries(self):
        """find all executable elf files in the image directory"""
        if self._installed_binaries:
            return self._installed_binaries
        binaries = {}
        d_len = len(self.d)
        re_so = re.compile(r'.*\.so[.0-9]*$')
        for root, _, files in os.walk(self.d, followlinks=False):
            for file in files:
                if os.path.islink(file):
                    continue
                if re_so.match(file):
                    continue
                abs_name = os.path.join(root, file)
                if os.access(abs_name, os.X_OK) and RecipeModified.is_elf_file(abs_name):
                    binary_path = abs_name[d_len:]
                    binary = ExecutableBinary(self.d, binary_path,
                                              self.systemd_services, self.init_scripts)
                    binaries[binary_path] = binary
        self._installed_binaries = dict(sorted(binaries.items()))
        return self._installed_binaries

    def _validate_requested_packages(self, args):
        """Raise if --package (once scoped to this recipe and expanded via
        parse_packages_arg) references a package this recipe doesn't produce.
        """
        packages = parse_packages_arg(getattr(args, 'package', None), self.bpn)
        for package in packages:
            if package not in self.packages_files:
                raise DevtoolError('Package "%s" is not one of the packages produced '
                                'by the %s recipe (PACKAGES: %s)' %
                                (package, self.pn, ' '.join(self.packages_files.keys())))

    def gen_deploy_target_script(self, args, deploy_target=None):
        """Generate a script which does what devtool deploy-target does

        This script is much quicker than devtool target-deploy. Because it
        does not need to start a bitbake server. All information from tinfoil
        is hard-coded in the generated script.

        deploy_target overrides args.target as the baked-in default, e.g. with
        the local NFS rootfs directory when --nfs was used (see devtool.deploy
        for how a directory target is handled without ssh). A runtime -t/--target
        can still override this default, same as without --nfs.
        """
        self._validate_requested_packages(args)
        cmd_lines = ['#!%s' % str(sys.executable)]
        cmd_lines.append('import sys')
        cmd_lines.append('devtool_sys_path = %s' % str(sys.path))
        cmd_lines.append('devtool_sys_path.reverse()')
        cmd_lines.append('for p in devtool_sys_path:')
        cmd_lines.append('    if p not in sys.path:')
        cmd_lines.append('        sys.path.insert(0, p)')
        cmd_lines.append('from devtool.deploy import deploy_no_d')
        args_filter = ['debug', 'dry_run', 'key', 'no_check_space', 'no_host_check',
                       'no_preserve', 'port', 'show_status', 'ssh_exec', 'strip', 'target']
        filtered_args_dict = {key: value for key, value in vars(
            args).items() if key in args_filter}
        if deploy_target:
            filtered_args_dict['target'] = deploy_target
        if is_loopback_target(filtered_args_dict['target']):
            filtered_args_dict['no_host_check'] = True
        cmd_lines.append('filtered_args_dict = %s' % str(filtered_args_dict))
        cmd_lines.append('class Dict2Class(object):')
        cmd_lines.append('    def __init__(self, my_dict):')
        cmd_lines.append('        for key in my_dict:')
        cmd_lines.append('            setattr(self, key, my_dict[key])')
        cmd_lines.append('filtered_args = Dict2Class(filtered_args_dict)')
        cmd_lines.append('packages_files = %s' % repr(list(self.packages_files.items())))
        cmd_lines.append('file_globs = %s' % repr(list(getattr(args, 'file_globs', None) or []) or None))
        cmd_lines.append('i = 1')
        cmd_lines.append('while i < len(sys.argv) - 1:')
        cmd_lines.append('    if sys.argv[i] in ("-t", "--target"):')
        cmd_lines.append('        setattr(filtered_args, "target", sys.argv[i + 1])')
        cmd_lines.append('        i += 2')
        cmd_lines.append('    elif sys.argv[i] in ("-P", "--port"):')
        cmd_lines.append('        setattr(filtered_args, "port", sys.argv[i + 1])')
        cmd_lines.append('        i += 2')
        cmd_lines.append('    elif sys.argv[i] in ("-g", "--file-glob"):')
        cmd_lines.append('        file_globs = (file_globs or []) + [sys.argv[i + 1]]')
        cmd_lines.append('        i += 2')
        cmd_lines.append('    elif sys.argv[i] in ("-p", "--package"):')
        cmd_lines.append('        packages = getattr(filtered_args, "package", None) or []')
        cmd_lines.append('        setattr(filtered_args, "package", packages + [sys.argv[i + 1]])')
        cmd_lines.append('        i += 2')
        cmd_lines.append('    else:')
        cmd_lines.append('        i += 1')
        cmd_lines.append(
            "if filtered_args.target.split('@')[-1] in %s:" % str(LOOPBACK_HOSTS))
        cmd_lines.append('    filtered_args.no_host_check = True')
        cmd_lines.append(
            'setattr(filtered_args, "recipename", "%s")' % self.bpn)
        cmd_lines.append('deploy_no_d("%s", "%s", "%s", "%s", "%s", "%s", %d, "%s", "%s", filtered_args, file_globs=file_globs, packages_files=packages_files)' %
                         (self.d, self.workdir, self.path, self.strip_cmd,
                          self.libdir, self.base_libdir, self.max_process,
                          self.fakerootcmd, self.fakerootenv))
        return self.write_script(cmd_lines, 'deploy_target')

    def gen_install_task_script(self):
        """Generate a script which runs do_install through BitBake."""
        cmd_lines = ['#!%s' % sys.executable]
        if self.cmd_compile:
            cmd_lines += ['import subprocess',
                          'subprocess.run(%s, cwd=%r, shell=True, check=True)' %
                          (repr(self.cmd_compile), self.real_srctree)]
        cmd_lines += ['import os',
                      'import sys',
                      'sys.path.insert(0, %r)' % os.path.realpath(
                          os.path.join(self.bitbakepath, '..', 'lib')),
                      'import bb.tinfoil',
                      'os.chdir(%r)' % self.topdir,
                      # A stale BBPATH from the caller's environment (e.g. a
                      # different, still-valid build dir) would otherwise take
                      # precedence over cwd when bitbake looks for
                      # conf/bblayers.conf (see bb.cookerdata.findConfigFile).
                      'os.environ["BBPATH"] = %r' % self.topdir,
                      'tinfoil = bb.tinfoil.Tinfoil()',
                      'try:',
                      '    tinfoil.prepare(config_only=False, quiet=2)',
                      '    tinfoil.run_prepared_task(%r, "do_install")' % self.pn,
                      'finally:',
                      '    tinfoil.shutdown()']
        return self.write_script(cmd_lines, 'bb_run_do_install')

    def gen_install_deploy_script(self, args, deploy_target=None):
        """Generate a script which does install and deploy"""
        cmd_lines = ['#!/bin/sh -e']
        cmd_lines.append(self.gen_install_task_script())
        cmd_lines.append(self.gen_deploy_target_script(args, deploy_target) + ' "$@"')

        return self.write_script(cmd_lines, 'install_and_deploy')

    def write_script(self, cmd_lines, script_name):
        bb.utils.mkdirhier(self.ide_sdk_scripts_dir)
        script_name_arch = script_name + '_' + self.recipe_id
        script_file = os.path.join(self.ide_sdk_scripts_dir, script_name_arch)
        with open(script_file, 'w') as script_f:
            script_f.write(os.linesep.join(cmd_lines))
        st = os.stat(script_file)
        os.chmod(script_file, st.st_mode | stat.S_IEXEC)
        return script_file

    @property
    def oe_init_build_env(self):
        """Find the init-build-env used for this setup"""
        # bitbake-setup mode
        bb_setup_init = os.path.join(self.topdir, RecipeModified.INIT_BUILD_ENV)
        if os.path.exists(bb_setup_init):
            return os.path.abspath(bb_setup_init)

        # poky mode
        oe_init_dir = self.oe_init_dir
        if oe_init_dir:
            return os.path.join(oe_init_dir, RecipeModified.OE_INIT_BUILD_ENV)
        return None

    @property
    def oe_init_dir(self):
        """Find the directory where the oe-init-build-env is located

        Assumption: There might be a layer with higher priority than poky
        which provides to oe-init-build-env in the layer's toplevel folder.
        """
        if not self.__oe_init_dir:
            for layer in reversed(self.bblayers):
                result = subprocess.run(
                    ['git', 'rev-parse', '--show-toplevel'], cwd=layer, capture_output=True)
                if result.returncode == 0:
                    oe_init_dir = result.stdout.decode('utf-8').strip()
                    oe_init_path = os.path.join(
                        oe_init_dir, RecipeModified.OE_INIT_BUILD_ENV)
                    if os.path.exists(oe_init_path):
                        logger.debug("Using %s from: %s" % (
                            RecipeModified.OE_INIT_BUILD_ENV, oe_init_path))
                        self.__oe_init_dir = oe_init_dir
                        break
            if not self.__oe_init_dir:
                logger.error("Cannot find the bitbake top level folder")
        return self.__oe_init_dir


def ide_setup(args, config, basepath, workspace):
    """Generate the IDE configuration for the workspace"""

    # Explicitely passing some special recipes does not make sense
    for recipe in args.recipenames:
        if recipe in ['meta-ide-support', 'build-sysroots']:
            raise DevtoolError("Invalid recipe: %s." % recipe)

    # Collect information about tasks which need to be bitbaked.
    # In modified mode the image build is held back until after
    # setup_modified_recipe() has assigned the debugger port numbers and
    # update_image_bbappend() has written the complete bbappend (including
    # QB_SLIRP_OPT). That way the image is built with a single, stable
    # recipe hash so that no basehash-changed warnings are emitted.
    bootstrap_tasks = []
    bootstrap_tasks_late = []
    image_bootstrap_tasks = []
    # Must happen before setup_tinfoil() so that every parse in this session
    # sees the same bbappend content. Which of the recipes is the image is only
    # known after parsing, so this covers all of them; for a recipe without an
    # ide-sdk section it is a no-op.
    orig_bbappend_contents = RecipeImage.strip_bbappend_sections(
        config, args.recipenames)
    orig_recipe_bbappend_contents = RecipeModified.strip_bbappend_sections(
        config, args.recipenames)
    tinfoil = setup_tinfoil(config_only=False, basepath=basepath)
    try:
        # define mode depending on recipes which need to be processed
        recipes_image_names = []
        recipes_modified_names = []
        recipes_other_names = []
        for recipe in args.recipenames:
            try:
                check_workspace_recipe(
                    workspace, recipe, bbclassextend=True)
                recipes_modified_names.append(recipe)
            except DevtoolError:
                recipe_d = parse_recipe(
                    config, tinfoil, recipe, appends=True, filter_workspace=False)
                if not recipe_d:
                    raise DevtoolError("Parsing recipe %s failed" % recipe)
                if bb.data.inherits_class('image', recipe_d):
                    recipes_image_names.append(recipe)
                else:
                    recipes_other_names.append(recipe)

        invalid_params = False
        if args.mode == DevtoolIdeMode.shared:
            if len(recipes_modified_names):
                logger.error("In shared sysroots mode modified recipes %s cannot be handled." % str(
                    recipes_modified_names))
                invalid_params = True
            if args.nfs:
                logger.error("--nfs is only supported in modified mode.")
                invalid_params = True
        if args.nfs_extract_dir and not args.nfs:
            logger.error("--nfs-extract-dir requires --nfs.")
            invalid_params = True
        if args.mode == DevtoolIdeMode.modified:
            if not recipes_modified_names:
                appends_dir = os.path.join(config.workspace_path, 'appends')
                recipes_modified_names = sorted(
                    bb.parse.vars_from_file(path, None)[0]
                    for path in glob.glob(os.path.join(appends_dir, '*.bbappend'))
                    if bb.parse.vars_from_file(path, None)[0] not in recipes_image_names)
                if recipes_modified_names:
                    logger.info(
                        "No modified recipes specified, using workspace bbappends from %s: %s",
                        appends_dir, ', '.join(recipes_modified_names))
            if len(recipes_other_names):
                logger.error("Only in shared sysroots mode not modified recipes %s can be handled." % str(
                    recipes_other_names))
                invalid_params = True
            if len(recipes_image_names) != 1:
                logger.error(
                    "One image recipe is required as the rootfs for the remote development.")
                invalid_params = True
            if not recipes_modified_names:
                logger.error(
                    "At least one modified recipe is required or must be guessable from %s." %
                    os.path.join(config.workspace_path, 'appends'))
                invalid_params = True
            for modified_recipe_name in recipes_modified_names:
                if modified_recipe_name.startswith('nativesdk-') or modified_recipe_name.endswith('-native'):
                    logger.error(
                        "Only cross compiled recipes are support. %s is not cross." % modified_recipe_name)
                    invalid_params = True

        if invalid_params:
            raise DevtoolError("Invalid parameters are passed.")

        # For the shared sysroots mode, add all dependencies of all the images to the sysroots
        # For the modified mode provide one rootfs and the corresponding debug symbols via rootfs-dbg
        nfs_export_base_dir = args.nfs_extract_dir or os.path.join(
            config.workspace_path, 'nfs-exports')
        recipes_images = []
        for recipes_image_name in recipes_image_names:
            logger.info("Using image: %s" % recipes_image_name)
            recipe_image = RecipeImage(
                recipes_image_name,
                orig_bbappend_contents.get(recipes_image_name))
            recipe_image.initialize(config, tinfoil)
            recipe_image.set_nfs_rootfs(nfs_export_base_dir, args.nfs)
            # With --skip-bitbake nothing is extracted, so the generated IDE
            # configuration would point at a directory that never appears.
            if args.nfs and args.skip_bitbake and not os.path.isdir(recipe_image.nfs_deploy_dir):
                raise DevtoolError(
                    "%s does not exist. Run devtool ide-sdk --nfs=%s without "
                    "--skip-bitbake first." % (recipe_image.nfs_deploy_dir, args.nfs))
            if args.mode == DevtoolIdeMode.modified:
                # Keep the image build separate so that the complete bbappend
                # (IMAGE_ vars + QB_SLIRP_OPT) can be written in one step
                # before the image is built, avoiding sstate hash mismatches.
                image_bootstrap_tasks += recipe_image.bootstrap_tasks
            else:
                bootstrap_tasks += recipe_image.bootstrap_tasks
            recipes_images.append(recipe_image)

        # Provide a Direct SDK with shared sysroots
        recipes_not_modified = []
        if args.mode == DevtoolIdeMode.shared:
            ide_support = RecipeMetaIdeSupport()
            ide_support.initialize(config, tinfoil)
            bootstrap_tasks += ide_support.bootstrap_tasks

            logger.info("Adding %s to the Direct SDK sysroots." %
                        str(recipes_other_names))
            for recipe_name in recipes_other_names:
                recipe_not_modified = RecipeNotModified(recipe_name)
                bootstrap_tasks += recipe_not_modified.bootstrap_tasks
                recipes_not_modified.append(recipe_not_modified)

            build_sysroots = RecipeBuildSysroots()
            build_sysroots.initialize(config, tinfoil)
            bootstrap_tasks_late += build_sysroots.bootstrap_tasks
            shared_env = SharedSysrootsEnv()
            shared_env.initialize(ide_support, build_sysroots)

        recipes_modified = []
        if args.mode == DevtoolIdeMode.modified:
            logger.info("Setting up workspaces for modified recipe: %s" %
                        str(recipes_modified_names))
            debuggers = {}
            for recipe_name in recipes_modified_names:
                recipe_modified = RecipeModified(
                    recipe_name, orig_recipe_bbappend_contents.get(recipe_name))
                recipe_modified.initialize(config, workspace, tinfoil)
                bootstrap_tasks += recipe_modified.bootstrap_tasks
                recipes_modified.append(recipe_modified)

                # Key by (arch, toolchain) so recipes with different toolchains
                # targeting the same arch each get the right debugger.
                debugger_key = (recipe_modified.target_arch,
                                recipe_modified.toolchain or '')
                if debugger_key not in debuggers:
                    target_device = TargetDevice(args)
                    if recipe_modified.toolchain == 'clang':
                        debugger = RecipeLldbNative(args, target_device)
                    else:
                        debugger = RecipeGdbCross(
                            args, recipe_modified.target_arch, target_device)
                    debugger.initialize(config, workspace, tinfoil)
                    bootstrap_tasks += debugger.bootstrap_tasks
                    debuggers[debugger_key] = debugger
                recipe_modified.debugger_cross = debuggers[debugger_key]

    finally:
        tinfoil.shutdown()

    bb_cmd = 'bitbake '
    if args.bitbake_k:
        bb_cmd += "-k "

    # Add back the clangd toolchain support section stripped above, if still
    # needed. Runs even with --skip-bitbake, otherwise it would be lost (see
    # RecipeModified.strip_bbappend_sections()).
    recipe_bbappend_changed = False
    for recipe_modified in recipes_modified:
        if recipe_modified.update_bbappend():
            recipe_bbappend_changed = True

    if not args.skip_bitbake:
        if recipe_bbappend_changed:
            # The bbappend content just written differs from the one bitbake
            # parsed during the tinfoil session above. See update_image_bbappend()'s
            # matching reset for phase 2 for why this is needed.
            reparse_tinfoil = setup_tinfoil(config_only=True, basepath=basepath)
            try:
                reparse_tinfoil.run_command('resetCooker')
                reparse_tinfoil.parse_recipes()
            finally:
                reparse_tinfoil.shutdown()

        # Phase 1: build modified recipes and debug tools so that
        # installed_binaries is populated and port numbers can be assigned.
        # The image is built in phase 2, after the complete bbappend is written.
        if bootstrap_tasks:
            exec_build_env_command(
                config.init_path, basepath,
                bb_cmd + ' '.join(bootstrap_tasks), watch=True)
        if bootstrap_tasks_late:
            exec_build_env_command(
                config.init_path, basepath,
                bb_cmd + ' '.join(bootstrap_tasks_late), watch=True)

    # Instantiate the active IDE plugin
    ide = ide_plugins[args.ide]()
    if args.mode == DevtoolIdeMode.shared:
        ide.setup_shared_sysroots(shared_env)
    elif args.mode == DevtoolIdeMode.modified:
        for recipe_modified in recipes_modified:
            if recipe_modified.build_tool is BuildTool.CMAKE:
                recipe_modified.cmake_preset()
            if recipe_modified.build_tool is BuildTool.MESON:
                recipe_modified.gen_meson_wrapper()
            ide.setup_modified_recipe(
                args, recipe_image, recipe_modified)

            if recipe_modified.wants_debug_build and recipe_modified.debug_build != '1':
                logger.warn(
                    'Recipe %s is compiled with release build configuration. '
                    'You might want to add DEBUG_BUILD = "1" to %s. '
                    'Note that devtool modify --debug-build can do this automatically.',
                    recipe_modified.name, recipe_modified.bbappend)

        # Ports are now assigned. Write the complete image bbappend --
        # IMAGE_ debug settings and QB_SLIRP_OPT -- in a single step so
        # that the image is built with exactly one recipe hash. This
        # avoids the sstate basehash-changed warnings that arise when
        # the bbappend is modified after the image has already been
        # built. This also runs with --skip-bitbake, otherwise the sections
        # removed by strip_bbappend_sections() would be lost.
        bbappend_changed = False
        for ri in recipes_images:
            if ri.update_image_bbappend(recipes_modified, args.nfs):
                bbappend_changed = True

        if not args.skip_bitbake:
            if image_bootstrap_tasks:
                if bbappend_changed:
                    # The bbappend content just written differs from the one
                    # bitbake parsed during the tinfoil session above. With a
                    # memory resident server that session's basehashes are
                    # still cached, so reparsing would report "basehash value
                    # changed ... not deterministic" for every task. Tell
                    # bitbake the recipe intentionally changed by resetting the
                    # cooker: this clears its basehash history.
                    reparse_tinfoil = setup_tinfoil(config_only=True, basepath=basepath)
                    try:
                        reparse_tinfoil.run_command('resetCooker')
                        reparse_tinfoil.parse_recipes()
                    finally:
                        reparse_tinfoil.shutdown()

                # Phase 2: build the image. do_image -> do_write_qemuboot_conf
                # picks up QB_SLIRP_OPT from the bbappend written above, so no
                # separate write_qemuboot_conf step is needed.
                exec_build_env_command(
                    config.init_path, basepath,
                    bb_cmd + ' '.join(image_bootstrap_tasks), watch=True)

        if args.nfs and not args.skip_bitbake:
            for ri in recipes_images:
                ri.extract_nfs_rootfs(nfs_export_base_dir, args.nfs, args.target)
    else:
        raise DevtoolError("Must not end up here.")


def register_commands(subparsers, context):
    """Register devtool subcommands from this plugin"""

    # The ide-sdk command bootstraps the SDK from the bitbake environment before the IDE
    # configuration is generated. In the case of the eSDK, the bootstrapping is performed
    # during the installation of the eSDK installer. Running the ide-sdk plugin from an
    # eSDK installer-based setup would require skipping the bootstrapping and probably
    # taking some other differences into account when generating the IDE configurations.
    # This would be possible. But it is not implemented.
    if context.fixed_setup:
        return

    global ide_plugins

    # Search for IDE plugins in all sub-folders named ide_plugins where devtool seraches for plugins.
    pluginpaths = [os.path.join(path, 'ide_plugins')
                   for path in context.pluginpaths]
    ide_plugin_modules = []
    for pluginpath in pluginpaths:
        scriptutils.load_plugins(logger, ide_plugin_modules, pluginpath)

    for ide_plugin_module in ide_plugin_modules:
        if hasattr(ide_plugin_module, 'register_ide_plugin'):
            ide_plugin_module.register_ide_plugin(ide_plugins)
    # Sort plugins according to their priority. The first entry is the default IDE plugin.
    ide_plugins = dict(sorted(ide_plugins.items(),
                       key=lambda p: p[1].ide_plugin_priority(), reverse=True))

    parser_ide_sdk = subparsers.add_parser('ide-sdk', group='working', order=50, formatter_class=RawTextHelpFormatter,
                                           help='Setup the SDK and configure the IDE')
    parser_ide_sdk.add_argument(
        'recipenames', nargs='+', help='Generate an IDE configuration suitable to work on the given recipes.\n'
        'Depending on the --mode parameter different types of SDKs and IDE configurations are generated.\n'
        'In modified mode at least the image recipe is required; if no modified recipe is passed, '
        'all modified recipes are taken from <workspace>/appends/*.bbappend.')
    parser_ide_sdk.add_argument(
        '-m', '--mode', type=DevtoolIdeMode, default=DevtoolIdeMode.modified,
        help='Different SDK types are supported:\n'
        '- "' + DevtoolIdeMode.modified.name + '" (default):\n'
        '  devtool modify creates a workspace to work on the source code of a recipe.\n'
        '  devtool ide-sdk builds the SDK and generates the IDE configuration(s) in the workspace directorie(s)\n'
        '  Usage example:\n'
        '    devtool modify cmake-example\n'
        '    devtool ide-sdk cmake-example core-image-minimal\n'
        '    Start the IDE in the workspace folder\n'
        '  At least one devtool modified recipe plus one image recipe are required:\n'
        '  The image recipe is used to generate the target image and the remote debug configuration.\n'
        '- "' + DevtoolIdeMode.shared.name + '":\n'
        '  Usage example:\n'
        '    devtool ide-sdk -m ' + DevtoolIdeMode.shared.name + ' recipe(s)\n'
        '  This command generates a cross-toolchain as well as the corresponding shared sysroot directories.\n'
        '  To use this tool-chain the environment-* file found in the deploy..image folder needs to be sourced into a shell.\n'
        '  In case of VSCode and cmake the tool-chain is also exposed as a cmake-kit')
    default_ide = list(ide_plugins.keys())[0]
    parser_ide_sdk.add_argument(
        '-i', '--ide', choices=ide_plugins.keys(), default=default_ide,
        help='Setup the configuration for this IDE (default: %s)' % default_ide)
    parser_ide_sdk.add_argument(
        '-t', '--target', default='root@192.168.7.2',
        help='Live target machine running an ssh server: user@hostname.')
    parser_ide_sdk.add_argument(
        '-G', '--gdbserver-port-start', default="1234", help='port where gdbserver is listening.')
    parser_ide_sdk.add_argument(
        '-c', '--no-host-check', help='Disable ssh host key checking', action='store_true')
    parser_ide_sdk.add_argument(
        '-e', '--ssh-exec', help='Executable to use in place of ssh')
    parser_ide_sdk.add_argument(
        '-P', '--port', help='Specify ssh port to use for connection to the target')
    parser_ide_sdk.add_argument(
        '-I', '--key', help='Specify ssh private key for connection to the target')
    parser_ide_sdk.add_argument(
        '--nfs', choices=('rootfs', 'rootfs-dbg'),
        help='Build and extract the selected image rootfs below '
        '<workspace>/nfs-exports/<image-PN>/ '
        'for NFS booting.')
    parser_ide_sdk.add_argument(
        '--nfs-extract-dir', metavar='DIR',
        help='Extract the --nfs rootfs below DIR/<image-PN>/ instead of the default '
        '<workspace>/nfs-exports/<image-PN>/, regardless of whether --nfs=rootfs '
        'or --nfs=rootfs-dbg is selected. Requires --nfs.')
    parser_ide_sdk.add_argument(
        '--skip-bitbake', help='Skip the bitbake builds which update the SDK. The recipes are still parsed, '
        'the IDE configuration is generated from their metadata', action='store_true')
    parser_ide_sdk.add_argument(
        '-k', '--bitbake-k', help='Pass -k parameter to bitbake', action='store_true')
    parser_ide_sdk.add_argument(
        '--no-strip', help='Do not strip executables prior to deploy', dest='strip', action='store_false')
    parser_ide_sdk.add_argument(
        '-n', '--dry-run', help='List files to be undeployed only', action='store_true')
    parser_ide_sdk.add_argument(
        '-s', '--show-status', help='Show progress/status output', action='store_true')
    parser_ide_sdk.add_argument(
        '-p', '--no-preserve', help='Do not preserve existing files', action='store_true')
    parser_ide_sdk.add_argument(
        '--no-check-space', help='Do not check for available space before deploying', action='store_true')
    parser_ide_sdk.add_argument(
        '--package', action='append', metavar='PACKAGE',
        help='Only deploy files belonging to PACKAGE, as defined by that package\'s '
        'FILES variable in the recipe metadata. May be a comma-separated list '
        'and/or specified multiple times. May be prefixed with "RECIPE:" to target '
        'one of several recipes at once, e.g. "RECIPE:,-doc,-ptest" is short for '
        '"RECIPE,RECIPE-doc,RECIPE-ptest".')
    parser_ide_sdk.add_argument(
        '--file-glob', action='append', dest='file_globs', metavar='GLOB',
        help='Only deploy files whose installed path matches this glob pattern '
        '(e.g. "/usr/bin/*"). May be specified multiple times. Combined with '
        '--package if both are given. May be prefixed with "RECIPE:" to scope '
        'the entry to one of the recipes being processed e.g. "RECIPE:/usr/bin/*".')
    parser_ide_sdk.set_defaults(func=ide_setup)
