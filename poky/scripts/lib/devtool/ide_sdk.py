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
from argparse import RawTextHelpFormatter
from enum import Enum

import scriptutils
import bb
from devtool import exec_build_env_command, setup_tinfoil, check_workspace_recipe, DevtoolError, parse_recipe
from devtool.standard import get_real_srctree
from devtool.ide_plugins import BuildTool


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


class TargetDevice:
    """SSH remote login parameters"""

    def __init__(self, args):
        self.extraoptions = ''
        if args.no_host_check:
            self.extraoptions += '-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
        self.ssh_sshexec = 'ssh'
        if args.ssh_exec:
            self.ssh_sshexec = args.ssh_exec
        self.ssh_port = ''
        if args.port:
            self.ssh_port = "-p %s" % args.port
        if args.key:
            self.extraoptions += ' -i %s' % args.key

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
    """Handle handle gdb-cross on the host and the gdbserver on the target device"""

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
        self.gdbserver_path = self.__find_gdbserver(config, tinfoil)

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

    def __init__(self, name):
        self.combine_dbg_image = False
        self.gdbserver_missing = False
        self.name = name
        self.rootfs = None
        self.__rootfs_dbg = None
        self.bootstrap_tasks = [self.name + ':do_build']

    def initialize(self, config, tinfoil):
        image_d = parse_recipe(
            config, tinfoil, self.name, appends=True, filter_workspace=False)
        if not image_d:
            raise DevtoolError(
                "Parsing image recipe %s failed" % self.name)

        self.combine_dbg_image = bb.data.inherits_class(
            'image-combined-dbg', image_d)

        workdir = image_d.getVar('WORKDIR')
        self.rootfs = os.path.join(workdir, 'rootfs')
        if image_d.getVar('IMAGE_GEN_DEBUGFS') == "1":
            self.__rootfs_dbg = os.path.join(workdir, 'rootfs-dbg')

        self.gdbserver_missing = 'gdbserver' not in image_d.getVar(
            'IMAGE_INSTALL') and 'tools-debug' not in image_d.getVar('IMAGE_FEATURES')

    @property
    def debug_support(self):
        return bool(self.rootfs_dbg)

    @property
    def rootfs_dbg(self):
        if self.__rootfs_dbg and os.path.isdir(self.__rootfs_dbg):
            return self.__rootfs_dbg
        return None


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


class RecipeModified:
    """Handling of recipes in the workspace created by devtool modify"""
    OE_INIT_BUILD_ENV = 'oe-init-build-env'

    VALID_BASH_ENV_NAME_CHARS = re.compile(r"^[a-zA-Z0-9_]*$")

    def __init__(self, name):
        self.name = name
        self.bootstrap_tasks = [name + ':do_install']
        self.gdb_cross = None
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
        self.bpn = None
        self.d = None
        self.debug_build = None
        self.fakerootcmd = None
        self.fakerootenv = None
        self.libdir = None
        self.max_process = None
        self.package_arch = None
        self.package_debug_split_style = None
        self.path = None
        self.pn = None
        self.recipe_sysroot = None
        self.recipe_sysroot_native = None
        self.staging_incdir = None
        self.strip_cmd = None
        self.target_arch = None
        self.target_dbgsrc_dir = None
        self.topdir = None
        self.workdir = None
        self.recipe_id = None
        # replicate bitbake build environment
        self.exported_vars = None
        self.cmd_compile = None
        self.__oe_init_dir = None
        # main build tool used by this recipe
        self.build_tool = BuildTool.UNDEFINED
        # build_tool = cmake
        self.oecmake_generator = None
        self.cmake_cache_vars = None
        # build_tool = meson
        self.meson_buildtype = None
        self.meson_wrapper = None
        self.mesonopts = None
        self.extra_oemeson = None
        self.meson_cross_file = None

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
            self.srctree, recipe_d.getVar('S'), recipe_d.getVar('WORKDIR'))
        self.bbappend = workspace[workspacepn]['bbappend']

        self.ide_sdk_dir = os.path.join(
            config.workspace_path, 'ide-sdk', self.name)
        if os.path.exists(self.ide_sdk_dir):
            shutil.rmtree(self.ide_sdk_dir)
        self.ide_sdk_scripts_dir = os.path.join(self.ide_sdk_dir, 'scripts')

        self.b = recipe_d.getVar('B')
        self.base_libdir = recipe_d.getVar('base_libdir')
        self.bblayers = recipe_d.getVar('BBLAYERS').split()
        self.bpn = recipe_d.getVar('BPN')
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
        self.path = recipe_d.getVar('PATH')
        self.pn = recipe_d.getVar('PN')
        self.recipe_sysroot = os.path.realpath(
            recipe_d.getVar('RECIPE_SYSROOT'))
        self.recipe_sysroot_native = os.path.realpath(
            recipe_d.getVar('RECIPE_SYSROOT_NATIVE'))
        self.staging_bindir_toolchain = os.path.realpath(
            recipe_d.getVar('STAGING_BINDIR_TOOLCHAIN'))
        self.staging_incdir = os.path.realpath(
            recipe_d.getVar('STAGING_INCDIR'))
        self.strip_cmd = recipe_d.getVar('STRIP')
        self.target_arch = recipe_d.getVar('TARGET_ARCH')
        self.target_dbgsrc_dir = recipe_d.getVar('TARGET_DBGSRC_DIR')
        self.topdir = recipe_d.getVar('TOPDIR')
        self.workdir = os.path.realpath(recipe_d.getVar('WORKDIR'))

        self.__init_exported_variables(recipe_d)

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

    def solib_search_path(self, image):
        """Search for debug symbols in the rootfs and rootfs-dbg

        The debug symbols of shared libraries which are provided by other packages
        are grabbed from the -dbg packages in the rootfs-dbg.

        But most cross debugging tools like gdb, perf, and systemtap need to find
        executable/library first and through it debuglink note find corresponding
        symbols file. Therefore the library paths from the rootfs are added as well.

        Note: For the devtool modified recipe compiled from the IDE, the debug
        symbols are taken from the unstripped binaries in the image folder.
        Also, devtool deploy-target takes the files from the image folder.
        debug symbols in the image folder refer to the corresponding source files
        with absolute paths of the build machine. Debug symbols found in the
        rootfs-dbg are relocated and contain paths which refer to the source files
        installed on the target device e.g. /usr/src/...
        """
        base_libdir = self.base_libdir.lstrip('/')
        libdir = self.libdir.lstrip('/')
        so_paths = [
            # debug symbols for package_debug_split_style: debug-with-srcpkg or .debug
            os.path.join(image.rootfs_dbg, base_libdir, ".debug"),
            os.path.join(image.rootfs_dbg, libdir, ".debug"),
            # debug symbols for package_debug_split_style: debug-file-directory
            os.path.join(image.rootfs_dbg, "usr", "lib", "debug"),

            # The binaries are required as well, the debug packages are not enough
            # With image-combined-dbg.bbclass the binaries are copied into rootfs-dbg
            os.path.join(image.rootfs_dbg, base_libdir),
            os.path.join(image.rootfs_dbg, libdir),
            # Without image-combined-dbg.bbclass the binaries are only in rootfs.
            # Note: Stepping into source files located in rootfs-dbg does not
            #       work without image-combined-dbg.bbclass yet.
            os.path.join(image.rootfs, base_libdir),
            os.path.join(image.rootfs, libdir)
        ]
        return so_paths

    def solib_search_path_str(self, image):
        """Return a : separated list of paths usable by GDB's set solib-search-path"""
        return ':'.join(self.solib_search_path(image))

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

    def find_installed_binaries(self):
        """find all executable elf files in the image directory"""
        binaries = []
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
                    binaries.append(abs_name[d_len:])
        return sorted(binaries)

    def gen_deploy_target_script(self, args):
        """Generate a script which does what devtool deploy-target does

        This script is much quicker than devtool target-deploy. Because it
        does not need to start a bitbake server. All information from tinfoil
        is hard-coded in the generated script.
        """
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
        cmd_lines.append('filtered_args_dict = %s' % str(filtered_args_dict))
        cmd_lines.append('class Dict2Class(object):')
        cmd_lines.append('    def __init__(self, my_dict):')
        cmd_lines.append('        for key in my_dict:')
        cmd_lines.append('            setattr(self, key, my_dict[key])')
        cmd_lines.append('filtered_args = Dict2Class(filtered_args_dict)')
        cmd_lines.append(
            'setattr(filtered_args, "recipename", "%s")' % self.bpn)
        cmd_lines.append('deploy_no_d("%s", "%s", "%s", "%s", "%s", "%s", %d, "%s", "%s", filtered_args)' %
                         (self.d, self.workdir, self.path, self.strip_cmd,
                          self.libdir, self.base_libdir, self.max_process,
                          self.fakerootcmd, self.fakerootenv))
        return self.write_script(cmd_lines, 'deploy_target')

    def gen_install_deploy_script(self, args):
        """Generate a script which does install and deploy"""
        cmd_lines = ['#!/bin/bash']

        # . oe-init-build-env $BUILDDIR
        # Note: Sourcing scripts with arguments requires bash
        cmd_lines.append('cd "%s" || { echo "cd %s failed"; exit 1; }' % (
            self.oe_init_dir, self.oe_init_dir))
        cmd_lines.append('. "%s" "%s" || { echo ". %s %s failed"; exit 1; }' % (
            self.oe_init_build_env, self.topdir, self.oe_init_build_env, self.topdir))

        # bitbake -c install
        cmd_lines.append(
            'bitbake %s -c install --force || { echo "bitbake %s -c install --force failed"; exit 1; }' % (self.bpn, self.bpn))

        # Self contained devtool deploy-target
        cmd_lines.append(self.gen_deploy_target_script(args))

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
        """Find the oe-init-build-env used for this setup"""
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

    # Collect information about tasks which need to be bitbaked
    bootstrap_tasks = []
    bootstrap_tasks_late = []
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
        if args.mode == DevtoolIdeMode.modified:
            if len(recipes_other_names):
                logger.error("Only in shared sysroots mode not modified recipes %s can be handled." % str(
                    recipes_other_names))
                invalid_params = True
            if len(recipes_image_names) != 1:
                logger.error(
                    "One image recipe is required as the rootfs for the remote development.")
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
        recipes_images = []
        for recipes_image_name in recipes_image_names:
            logger.info("Using image: %s" % recipes_image_name)
            recipe_image = RecipeImage(recipes_image_name)
            recipe_image.initialize(config, tinfoil)
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
            gdbs_cross = {}
            for recipe_name in recipes_modified_names:
                recipe_modified = RecipeModified(recipe_name)
                recipe_modified.initialize(config, workspace, tinfoil)
                bootstrap_tasks += recipe_modified.bootstrap_tasks
                recipes_modified.append(recipe_modified)

                if recipe_modified.target_arch not in gdbs_cross:
                    target_device = TargetDevice(args)
                    gdb_cross = RecipeGdbCross(
                        args, recipe_modified.target_arch, target_device)
                    gdb_cross.initialize(config, workspace, tinfoil)
                    bootstrap_tasks += gdb_cross.bootstrap_tasks
                    gdbs_cross[recipe_modified.target_arch] = gdb_cross
                recipe_modified.gdb_cross = gdbs_cross[recipe_modified.target_arch]

    finally:
        tinfoil.shutdown()

    if not args.skip_bitbake:
        bb_cmd = 'bitbake '
        if args.bitbake_k:
            bb_cmd += "-k "
        bb_cmd_early = bb_cmd + ' '.join(bootstrap_tasks)
        exec_build_env_command(
            config.init_path, basepath, bb_cmd_early, watch=True)
        if bootstrap_tasks_late:
            bb_cmd_late = bb_cmd + ' '.join(bootstrap_tasks_late)
            exec_build_env_command(
                config.init_path, basepath, bb_cmd_late, watch=True)

    for recipe_image in recipes_images:
        if (recipe_image.gdbserver_missing):
            logger.warning(
                "gdbserver not installed in image %s. Remote debugging will not be available" % recipe_image)

        if recipe_image.combine_dbg_image is False:
            logger.warning(
                'IMAGE_CLASSES += "image-combined-dbg" is missing for image %s. Remote debugging will not find debug symbols from rootfs-dbg.' % recipe_image)

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

            if recipe_modified.debug_build != '1':
                logger.warn(
                    'Recipe %s is compiled with release build configuration. '
                    'You might want to add DEBUG_BUILD = "1" to %s. '
                    'Note that devtool modify --debug-build can do this automatically.',
                    recipe_modified.name, recipe_modified.bbappend)
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
        'Depending on the --mode parameter different types of SDKs and IDE configurations are generated.')
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
        '--skip-bitbake', help='Generate IDE configuration but skip calling bitbake to update the SDK', action='store_true')
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
    parser_ide_sdk.set_defaults(func=ide_setup)
