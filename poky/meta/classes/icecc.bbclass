#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Icecream distributed compiling support
#
# Stages directories with symlinks from gcc/g++ to icecc, for both
# native and cross compilers. Depending on each configure or compile,
# the directories are added at the head of the PATH list and ICECC_CXX
# and ICECC_CC are set.
#
# For the cross compiler, creates a tar.gz of our toolchain and sets
# ICECC_VERSION accordingly.
#
# The class now handles all 3 different compile 'stages' (i.e native ,cross-kernel and target) creating the
# necessary environment tar.gz file to be used by the remote machines.
# It also supports meta-toolchain generation.
#
# If ICECC_PATH is not set in local.conf then the class will try to locate it using 'bb.utils.which'
# but nothing is sure. ;)
#
# If ICECC_ENV_EXEC is set in local.conf, then it should point to the icecc-create-env script provided by the user
# or the default one provided by icecc-create-env_0.1.bb will be used.
# (NOTE that this is a modified version of the needed script and *not the one that comes with icecream*).
#
# User can specify if specific recipes or recipes inheriting specific classes should not use icecc to distribute
# compile jobs to remote machines, but handle them locally by defining ICECC_CLASS_DISABLE and ICECC_RECIPE_DISABLE
# with the appropriate values in local.conf. In addition the user can force to enable icecc for recipes
# which set an empty PARALLEL_MAKE variable by defining ICECC_RECIPE_ENABLE.
#
#########################################################################################
# Error checking is kept to minimum so double check any parameters you pass to the class
#########################################################################################

BB_BASEHASH_IGNORE_VARS += "ICECC_PARALLEL_MAKE ICECC_DISABLED ICECC_RECIPE_DISABLE \
    ICECC_CLASS_DISABLE ICECC_RECIPE_ENABLE ICECC_PATH ICECC_ENV_EXEC \
    ICECC_CARET_WORKAROUND ICECC_CFLAGS ICECC_ENV_VERSION \
    ICECC_DEBUG ICECC_LOGFILE ICECC_REPEAT_RATE ICECC_PREFERRED_HOST \
    ICECC_CLANG_REMOTE_CPP ICECC_IGNORE_UNVERIFIED ICECC_TEST_SOCKET \
    ICECC_ENV_DEBUG ICECC_REMOTE_CPP \
    "

ICECC_ENV_EXEC ?= "${STAGING_BINDIR_NATIVE}/icecc-create-env"

HOSTTOOLS_NONFATAL += "icecc patchelf"

# This version can be incremented when changes are made to the environment that
# invalidate the version on the compile nodes. Changing it will cause a new
# environment to be created.
#
# A useful thing to do for testing icecream changes locally is to add a
# subversion in local.conf:
#  ICECC_ENV_VERSION:append = "-my-ver-1"
ICECC_ENV_VERSION = "2"

# Default to disabling the caret workaround, If set to "1" in local.conf, icecc
# will locally recompile any files that have warnings, which can adversely
# affect performance.
#
# See: https://github.com/icecc/icecream/issues/190
export ICECC_CARET_WORKAROUND ??= "0"

export ICECC_REMOTE_CPP ??= "0"

ICECC_CFLAGS = ""
CFLAGS += "${ICECC_CFLAGS}"
CXXFLAGS += "${ICECC_CFLAGS}"

# Debug flags when generating environments
ICECC_ENV_DEBUG ??= ""

# Disable recipe list contains a list of recipes that can not distribute
# compile tasks for one reason or the other. When adding a new entry, please
# document why (how it failed) so that we can re-evaluate it later e.g. when
# there is a new version.
#
# libgcc-initial - fails with CPP sanity check error if host sysroot contains
#                  cross gcc built for another target tune/variant.
# pixman - prng_state: TLS reference mismatches non-TLS reference, possibly due to
#          pragma omp threadprivate(prng_state).
# systemtap - _HelperSDT.c undefs macros and uses the identifiers in macros emitting
#             inline assembly.
# target-sdk-provides-dummy - ${HOST_PREFIX} is empty which triggers the "NULL
#                             prefix" error.
ICECC_RECIPE_DISABLE += "\
    libgcc-initial \
    pixman \
    systemtap \
    target-sdk-provides-dummy \
    "

# Classes that should not use icecc. When adding a new entry, please
# document why (how it failed) so that we can re-evaluate it later.
#
# image - images aren't compiling, but the testing framework for images captures
#         PARALLEL_MAKE as part of the test environment. Many tests won't use
#         icecream, but leaving the high level of parallelism can cause them to
#         consume an unnecessary amount of resources.
ICECC_CLASS_DISABLE += "\
    image \
    "

def get_icecc_dep(d):
    # INHIBIT_DEFAULT_DEPS doesn't apply to the patch command. Whether or not
    # we need that built is the responsibility of the patch function / class, not
    # the application.
    if not d.getVar('INHIBIT_DEFAULT_DEPS'):
        return "icecc-create-env-native"
    return ""

DEPENDS:prepend = "${@get_icecc_dep(d)} "

get_cross_kernel_cc[vardepsexclude] += "KERNEL_CC"
def get_cross_kernel_cc(bb,d):
    if not icecc_is_kernel(bb, d):
        return None

    # evaluate the expression by the shell if necessary
    kernel_cc = d.getVar('KERNEL_CC')
    if '`' in kernel_cc or '$(' in kernel_cc:
        import subprocess
        kernel_cc = subprocess.check_output("echo %s" % kernel_cc, shell=True).decode("utf-8")[:-1]

    kernel_cc = kernel_cc.replace('ccache', '').strip()
    kernel_cc = kernel_cc.split(' ')[0]
    kernel_cc = kernel_cc.strip()
    return kernel_cc

def get_icecc(d):
    return d.getVar('ICECC_PATH') or bb.utils.which(os.getenv("PATH"), "icecc")

def use_icecc(bb,d):
    if d.getVar('ICECC_DISABLED') == "1":
        # don't even try it, when explicitly disabled
        return "no"

    # allarch recipes don't use compiler
    if icecc_is_allarch(bb, d):
        return "no"

    if icecc_is_cross_canadian(bb, d):
        return "no"

    pn = d.getVar('PN')
    bpn = d.getVar('BPN')

    # Enable/disable checks are made against BPN, because there is a good
    # chance that if icecc should be skipped for a recipe, it should be skipped
    # for all the variants of that recipe. PN is still checked in case a user
    # specified a more specific recipe.
    check_pn = set([pn, bpn])

    class_disable = (d.getVar('ICECC_CLASS_DISABLE') or "").split()

    for bbclass in class_disable:
        if bb.data.inherits_class(bbclass, d):
            bb.debug(1, "%s: bbclass %s found in disable, disable icecc" % (pn, bbclass))
            return "no"

    disabled_recipes = (d.getVar('ICECC_RECIPE_DISABLE') or "").split()
    enabled_recipes = (d.getVar('ICECC_RECIPE_ENABLE') or "").split()

    if check_pn & set(disabled_recipes):
        bb.debug(1, "%s: found in disable list, disable icecc" % pn)
        return "no"

    if check_pn & set(enabled_recipes):
        bb.debug(1, "%s: found in enabled recipes list, enable icecc" % pn)
        return "yes"

    if d.getVar('PARALLEL_MAKE') == "":
        bb.debug(1, "%s: has empty PARALLEL_MAKE, disable icecc" % pn)
        return "no"

    return "yes"

def icecc_is_allarch(bb, d):
    return d.getVar("PACKAGE_ARCH") == "all"

def icecc_is_kernel(bb, d):
    return \
        bb.data.inherits_class("kernel", d);

def icecc_is_native(bb, d):
    return \
        bb.data.inherits_class("cross", d) or \
        bb.data.inherits_class("native", d);

def icecc_is_cross_canadian(bb, d):
    return bb.data.inherits_class("cross-canadian", d)

def icecc_dir(bb, d):
    return d.expand('${TMPDIR}/work-shared/ice')

# Don't pollute allarch signatures with TARGET_FPU
icecc_version[vardepsexclude] += "TARGET_FPU"
def icecc_version(bb, d):
    if use_icecc(bb, d) == "no":
        return ""

    parallel = d.getVar('ICECC_PARALLEL_MAKE') or ""
    if not d.getVar('PARALLEL_MAKE') == "" and parallel:
        d.setVar("PARALLEL_MAKE", parallel)

    # Disable showing the caret in the GCC compiler output if the workaround is
    # disabled
    if d.getVar('ICECC_CARET_WORKAROUND') == '0':
        d.setVar('ICECC_CFLAGS', '-fno-diagnostics-show-caret')

    if icecc_is_native(bb, d):
        archive_name = "local-host-env"
    elif d.expand('${HOST_PREFIX}') == "":
        bb.fatal(d.expand("${PN}"), " NULL prefix")
    else:
        prefix = d.expand('${HOST_PREFIX}' )
        distro = d.expand('${DISTRO}')
        target_sys = d.expand('${TARGET_SYS}')
        float = d.getVar('TARGET_FPU') or "hard"
        archive_name = prefix + distro + "-"        + target_sys + "-" + float
        if icecc_is_kernel(bb, d):
            archive_name += "-kernel"

    import socket
    ice_dir = icecc_dir(bb, d)
    tar_file = os.path.join(ice_dir, "{archive}-{version}-@VERSION@-{hostname}.tar.gz".format(
        archive=archive_name,
        version=d.getVar('ICECC_ENV_VERSION'),
        hostname=socket.gethostname()
        ))

    return tar_file

def icecc_path(bb,d):
    if use_icecc(bb, d) == "no":
        # don't create unnecessary directories when icecc is disabled
        return

    staging = os.path.join(d.expand('${STAGING_BINDIR}'), "ice")
    if icecc_is_kernel(bb, d):
        staging += "-kernel"

    return staging

def icecc_get_external_tool(bb, d, tool):
    external_toolchain_bindir = d.expand('${EXTERNAL_TOOLCHAIN}${bindir_cross}')
    target_prefix = d.expand('${TARGET_PREFIX}')
    return os.path.join(external_toolchain_bindir, '%s%s' % (target_prefix, tool))

def icecc_get_tool_link(tool, d):
    import subprocess
    try:
        return subprocess.check_output("readlink -f %s" % tool, shell=True).decode("utf-8")[:-1]
    except subprocess.CalledProcessError as e:
        bb.note("icecc: one of the tools probably disappeared during recipe parsing, cmd readlink -f %s returned %d:\n%s" % (tool, e.returncode, e.output.decode("utf-8")))
        return tool

def icecc_get_path_tool(tool, d):
    # This is a little ugly, but we want to make sure we add an actual
    # compiler to the toolchain, not ccache. Some distros (e.g. Fedora)
    # have ccache enabled by default using symlinks in PATH, meaning ccache
    # would be found first when looking for the compiler.
    paths = os.getenv("PATH").split(':')
    while True:
        p, hist = bb.utils.which(':'.join(paths), tool, history=True)
        if not p or os.path.basename(icecc_get_tool_link(p, d)) != 'ccache':
            return p
        paths = paths[len(hist):]

    return ""

# Don't pollute native signatures with target TUNE_PKGARCH through STAGING_BINDIR_TOOLCHAIN
icecc_get_tool[vardepsexclude] += "STAGING_BINDIR_TOOLCHAIN"
def icecc_get_tool(bb, d, tool):
    if icecc_is_native(bb, d):
        return icecc_get_path_tool(tool, d)
    elif icecc_is_kernel(bb, d):
        return icecc_get_path_tool(get_cross_kernel_cc(bb, d), d)
    else:
        ice_dir = d.expand('${STAGING_BINDIR_TOOLCHAIN}')
        target_sys = d.expand('${TARGET_SYS}')
        for p in ice_dir.split(':'):
            tool_bin = os.path.join(p, "%s-%s" % (target_sys, tool))
            if os.path.isfile(tool_bin):
                return tool_bin
        external_tool_bin = icecc_get_external_tool(bb, d, tool)
        if os.path.isfile(external_tool_bin):
            return external_tool_bin
        return ""

def icecc_get_and_check_tool(bb, d, tool):
    # Check that g++ or gcc is not a symbolic link to icecc binary in
    # PATH or icecc-create-env script will silently create an invalid
    # compiler environment package.
    t = icecc_get_tool(bb, d, tool)
    if t:
        link_path = icecc_get_tool_link(t, d)
        if link_path == get_icecc(d):
            bb.error("%s is a symlink to %s in PATH and this prevents icecc from working" % (t, link_path))
            return ""
        else:
            return t
    else:
        return t

set_icecc_env[vardepsexclude] += "KERNEL_CC"
python set_icecc_env() {
    import os
    import subprocess

    if use_icecc(bb, d) == "no":
        return
    ICECC_VERSION = icecc_version(bb, d)
    if not ICECC_VERSION:
        bb.warn("Cannot use icecc: could not get ICECC_VERSION")
        return

    ICE_PATH = icecc_path(bb, d)
    if not ICE_PATH:
        bb.warn("Cannot use icecc: could not get ICE_PATH")
        return

    ICECC_BIN = get_icecc(d)
    if not ICECC_BIN:
        bb.warn("Cannot use icecc: icecc binary not found")
        return

    if (not bb.utils.which(os.getenv("PATH"), "patchelf") and
        not bb.utils.which(os.getenv("PATH"), "patchelf-uninative")):
        bb.warn("Cannot use icecc: patchelf not found")
        return

    ICECC_CC = icecc_get_and_check_tool(bb, d, "gcc")
    ICECC_CXX = icecc_get_and_check_tool(bb, d, "g++")
    # cannot use icecc_get_and_check_tool here because it assumes as without target_sys prefix
    ICECC_WHICH_AS = bb.utils.which(os.getenv('PATH'), 'as')
    if (not os.access(ICECC_CC, os.X_OK) or
        not os.access(ICECC_CXX, os.X_OK)):
        bb.note("Cannot use icecc: could not get ICECC_CC or ICECC_CXX")
        return

    cmd = []
    try:
        cmd = [ICECC_CC, '-dumpversion']
        ICE_VERSION = subprocess.check_output(cmd).decode("utf-8").strip()
    except subprocess.CalledProcessError as e:
        bb.warn("icecc: '{}' returned {}:\n{}".format(cmd, e.returncode, e.output.decode("utf-8")))
        return

    ICECC_VERSION = ICECC_VERSION.replace("@VERSION@", ICE_VERSION)

    if not os.access(d.getVar('ICECC_ENV_EXEC'), os.X_OK):
        bb.warn("Cannot use icecc: invalid ICECC_ENV_EXEC")
        return

    # Create symlinks to icecc and wrapper-scripts in the recipe-sysroot directory
    symlink_path = os.path.join(ICE_PATH, "symlinks")
    bb.utils.mkdirhier(symlink_path)
    compilers = []
    if icecc_is_kernel(bb, d):
        compilers.append(get_cross_kernel_cc(bb,d))
    else:
        host_prefix = d.getVar('HOST_PREFIX')
        compilers.extend([host_prefix + 'gcc', host_prefix + 'g++'])

    for compiler in compilers:
        try:
            os.symlink(ICECC_BIN, symlink_path + '/' + compiler)
        except FileExistsError:
            pass
        wrapper_script = os.path.join(ICE_PATH, compiler)
        with open(wrapper_script, 'w') as fd:
            fd.write("#!/bin/sh -e\n")
            fd.write("export ICECC_VERSION={}\n".format(ICECC_VERSION))
            fd.write("export ICECC_CC={}\n".format(ICECC_CC))
            fd.write("export ICECC_CXX={}\n".format(ICECC_CXX))
            fd.write("{} \"$@\"\n".format(os.path.join(ICE_PATH, "symlinks", compiler)))
        os.chmod(wrapper_script, 0o755)

    try:
        cmd = [ICECC_CC, '-print-prog-name=as']
        ICECC_AS = subprocess.check_output(cmd).decode("utf-8").strip()
    except subprocess.CalledProcessError as e:
        bb.warn("icecc: '{}' returned {}:\n{}".format(cmd, e.returncode, e.output.decode("utf-8")))
        return
    # for target recipes should return something like:
    # /OE/tmp-eglibc/sysroots/x86_64-linux/usr/libexec/arm920tt-oe-linux-gnueabi/gcc/arm-oe-linux-gnueabi/4.8.2/as
    # and just "as" for native, if it returns "as" in current directory (for whatever reason) use "as" from PATH
    if not os.path.dirname(ICECC_AS):
        ICECC_AS = ICECC_WHICH_AS

    if not os.path.isfile(ICECC_VERSION + ".done"):
        bb.utils.mkdirhier(os.path.dirname(ICECC_VERSION))

        # the ICECC_VERSION generation step must be locked by a mutex
        # in order to prevent race conditions
        lock = bb.utils.lockfile(ICECC_VERSION + '.lock')
        try:
            cmd = [d.getVar('ICECC_ENV_EXEC')]
            if d.getVar('ICECC_ENV_DEBUG'):
                cmd.append(d.getVar('ICECC_ENV_DEBUG'))
            cmd.extend([ICECC_CC, ICECC_CXX, ICECC_AS, ICECC_VERSION])
            subprocess.check_output(cmd)
            cmd = ['touch', ICECC_VERSION + '.done']
            subprocess.check_output(cmd)
        except subprocess.CalledProcessError as e:
            bb.warn("icecc: '{}' returned {}:\n{}".format(cmd, e.returncode, e.output.decode("utf-8")))
            bb.utils.unlockfile(lock)
            return
        bb.utils.unlockfile(lock)

    # Don't let ccache find the icecream compiler links that have been created, otherwise
    # it can end up invoking icecream recursively.
    d.setVar('CCACHE_PATH', d.getVar('PATH'))
    d.setVar('CCACHE_DISABLE', '1')

    d.prependVar('PATH', ICE_PATH + ':')

    bb.note("Using icecc path: {}".format(ICE_PATH))
    bb.note("Using icecc tarball: {}".format(ICECC_VERSION))
}

do_configure[prefuncs] += "set_icecc_env"
do_compile[prefuncs] += "set_icecc_env"
do_compile_kernelmodules[prefuncs] += "set_icecc_env"
do_install[prefuncs] += "set_icecc_env"

# Icecream is not (currently) supported in the extensible SDK
ICECC_SDK_HOST_TASK = "nativesdk-icecc-toolchain"
ICECC_SDK_HOST_TASK:task-populate-sdk-ext = ""

# Don't include icecream in uninative tarball
ICECC_SDK_HOST_TASK:pn-uninative-tarball = ""

# Add the toolchain scripts to the SDK
TOOLCHAIN_HOST_TASK:append = " ${ICECC_SDK_HOST_TASK}"

python () {
    if d.getVar('ICECC_DISABLED') != "1":
        for task in ['do_configure', 'do_compile', 'do_compile_kernelmodules', 'do_install']:
                d.setVarFlag(task, 'network', '1')
}
