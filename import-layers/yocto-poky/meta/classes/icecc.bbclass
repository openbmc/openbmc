# IceCream distributed compiling support
#
# Stages directories with symlinks from gcc/g++ to icecc, for both
# native and cross compilers. Depending on each configure or compile,
# the directories are added at the head of the PATH list and ICECC_CXX
# and ICEC_CC are set.
#
# For the cross compiler, creates a tar.gz of our toolchain and sets
# ICECC_VERSION accordingly.
#
# The class now handles all 3 different compile 'stages' (i.e native ,cross-kernel and target) creating the
# necessary environment tar.gz file to be used by the remote machines.
# It also supports meta-toolchain generation
#
# If ICECC_PATH is not set in local.conf then the class will try to locate it using 'bb.utils.which'
# but nothing is sure ;)
#
# If ICECC_ENV_EXEC is set in local.conf, then it should point to the icecc-create-env script provided by the user
# or the default one provided by icecc-create-env.bb will be used
# (NOTE that this is a modified version of the script need it and *not the one that comes with icecc*
#
# User can specify if specific packages or packages belonging to class should not use icecc to distribute
# compile jobs to remote machines, but handled locally, by defining ICECC_USER_CLASS_BL and ICECC_USER_PACKAGE_BL
# with the appropriate values in local.conf. In addition the user can force to enable icecc for packages
# which set an empty PARALLEL_MAKE variable by defining ICECC_USER_PACKAGE_WL.
#
#########################################################################################
#Error checking is kept to minimum so double check any parameters you pass to the class
###########################################################################################

BB_HASHBASE_WHITELIST += "ICECC_PARALLEL_MAKE ICECC_DISABLED ICECC_USER_PACKAGE_BL ICECC_USER_CLASS_BL ICECC_USER_PACKAGE_WL ICECC_PATH ICECC_ENV_EXEC"

ICECC_ENV_EXEC ?= "${STAGING_BINDIR_NATIVE}/icecc-create-env"

def icecc_dep_prepend(d):
    # INHIBIT_DEFAULT_DEPS doesn't apply to the patch command.  Whether or  not
    # we need that built is the responsibility of the patch function / class, not
    # the application.
    if not d.getVar('INHIBIT_DEFAULT_DEPS', False):
        return "icecc-create-env-native"
    return ""

DEPENDS_prepend += "${@icecc_dep_prepend(d)} "

get_cross_kernel_cc[vardepsexclude] += "KERNEL_CC"
def get_cross_kernel_cc(bb,d):
    kernel_cc = d.getVar('KERNEL_CC', False)

    # evaluate the expression by the shell if necessary
    if '`' in kernel_cc or '$(' in kernel_cc:
        import subprocess
        kernel_cc = subprocess.check_output("echo %s" % kernel_cc, shell=True).decode("utf-8")[:-1]

    kernel_cc = d.expand(kernel_cc)
    kernel_cc = kernel_cc.replace('ccache', '').strip()
    kernel_cc = kernel_cc.split(' ')[0]
    kernel_cc = kernel_cc.strip()
    return kernel_cc

def get_icecc(d):
    return d.getVar('ICECC_PATH', False) or bb.utils.which(os.getenv("PATH"), "icecc")

def create_path(compilers, bb, d):
    """
    Create Symlinks for the icecc in the staging directory
    """
    staging = os.path.join(d.expand('${STAGING_BINDIR}'), "ice")
    if icecc_is_kernel(bb, d):
        staging += "-kernel"

    #check if the icecc path is set by the user
    icecc = get_icecc(d)

    # Create the dir if necessary
    try:
        os.stat(staging)
    except:
        try:
            os.makedirs(staging)
        except:
            pass

    for compiler in compilers:
        gcc_path = os.path.join(staging, compiler)
        try:
            os.stat(gcc_path)
        except:
            try:
                os.symlink(icecc, gcc_path)
            except:
                pass

    return staging

def use_icecc(bb,d):
    if d.getVar('ICECC_DISABLED', False) == "1":
        # don't even try it, when explicitly disabled
        return "no"

    # allarch recipes don't use compiler
    if icecc_is_allarch(bb, d):
        return "no"

    pn = d.getVar('PN')

    system_class_blacklist = []
    user_class_blacklist = (d.getVar('ICECC_USER_CLASS_BL', False) or "none").split()
    package_class_blacklist = system_class_blacklist + user_class_blacklist

    for black in package_class_blacklist:
        if bb.data.inherits_class(black, d):
            bb.debug(1, "%s: class %s found in blacklist, disable icecc" % (pn, black))
            return "no"

    # "system" recipe blacklist contains a list of packages that can not distribute compile tasks
    # for one reason or the other
    # this is the old list (which doesn't seem to be valid anymore, because I was able to build
    # all these with icecc enabled)
    # system_package_blacklist = [ "glibc", "gcc", "bind", "u-boot", "dhcp-forwarder", "enchant", "connman", "orbit2" ]
    # when adding new entry, please document why (how it failed) so that we can re-evaluate it later
    # e.g. when there is new version
    # building libgcc-initial with icecc fails with CPP sanity check error if host sysroot contains cross gcc built for another target tune/variant
    system_package_blacklist = ["libgcc-initial"]
    user_package_blacklist = (d.getVar('ICECC_USER_PACKAGE_BL', False) or "").split()
    user_package_whitelist = (d.getVar('ICECC_USER_PACKAGE_WL', False) or "").split()
    package_blacklist = system_package_blacklist + user_package_blacklist

    if pn in package_blacklist:
        bb.debug(1, "%s: found in blacklist, disable icecc" % pn)
        return "no"

    if pn in user_package_whitelist:
        bb.debug(1, "%s: found in whitelist, enable icecc" % pn)
        return "yes"

    if d.getVar('PARALLEL_MAKE', False) == "":
        bb.debug(1, "%s: has empty PARALLEL_MAKE, disable icecc" % pn)
        return "no"

    return "yes"

def icecc_is_allarch(bb, d):
    return d.getVar("PACKAGE_ARCH") == "all" or bb.data.inherits_class('allarch', d)

def icecc_is_kernel(bb, d):
    return \
        bb.data.inherits_class("kernel", d);

def icecc_is_native(bb, d):
    return \
        bb.data.inherits_class("cross", d) or \
        bb.data.inherits_class("native", d);

# Don't pollute allarch signatures with TARGET_FPU
icecc_version[vardepsexclude] += "TARGET_FPU"
def icecc_version(bb, d):
    if use_icecc(bb, d) == "no":
        return ""

    parallel = d.getVar('ICECC_PARALLEL_MAKE', False) or ""
    if not d.getVar('PARALLEL_MAKE', False) == "" and parallel:
        d.setVar("PARALLEL_MAKE", parallel)

    if icecc_is_native(bb, d):
        archive_name = "local-host-env"
    elif d.expand('${HOST_PREFIX}') == "":
        bb.fatal(d.expand("${PN}"), " NULL prefix")
    else:
        prefix = d.expand('${HOST_PREFIX}' )
        distro = d.expand('${DISTRO}')
        target_sys = d.expand('${TARGET_SYS}')
        float = d.getVar('TARGET_FPU', False) or "hard"
        archive_name = prefix + distro + "-"        + target_sys + "-" + float
        if icecc_is_kernel(bb, d):
            archive_name += "-kernel"

    import socket
    ice_dir = d.expand('${STAGING_DIR_NATIVE}${prefix_native}')
    tar_file = os.path.join(ice_dir, 'ice', archive_name + "-@VERSION@-" + socket.gethostname() + '.tar.gz')

    return tar_file

def icecc_path(bb,d):
    if use_icecc(bb, d) == "no":
        # don't create unnecessary directories when icecc is disabled
        return

    if icecc_is_kernel(bb, d):
        return create_path( [get_cross_kernel_cc(bb,d), ], bb, d)

    else:
        prefix = d.expand('${HOST_PREFIX}')
        return create_path( [prefix+"gcc", prefix+"g++"], bb, d)

def icecc_get_external_tool(bb, d, tool):
    external_toolchain_bindir = d.expand('${EXTERNAL_TOOLCHAIN}${bindir_cross}')
    target_prefix = d.expand('${TARGET_PREFIX}')
    return os.path.join(external_toolchain_bindir, '%s%s' % (target_prefix, tool))

# Don't pollute native signatures with target TUNE_PKGARCH through STAGING_BINDIR_TOOLCHAIN
icecc_get_tool[vardepsexclude] += "STAGING_BINDIR_TOOLCHAIN"
def icecc_get_tool(bb, d, tool):
    if icecc_is_native(bb, d):
        return bb.utils.which(os.getenv("PATH"), tool)
    elif icecc_is_kernel(bb, d):
        return bb.utils.which(os.getenv("PATH"), get_cross_kernel_cc(bb, d))
    else:
        ice_dir = d.expand('${STAGING_BINDIR_TOOLCHAIN}')
        target_sys = d.expand('${TARGET_SYS}')
        tool_bin = os.path.join(ice_dir, "%s-%s" % (target_sys, tool))
        if os.path.isfile(tool_bin):
            return tool_bin
        else:
            external_tool_bin = icecc_get_external_tool(bb, d, tool)
            if os.path.isfile(external_tool_bin):
                return external_tool_bin
            else:
                return ""

def icecc_get_and_check_tool(bb, d, tool):
    # Check that g++ or gcc is not a symbolic link to icecc binary in
    # PATH or icecc-create-env script will silently create an invalid
    # compiler environment package.
    t = icecc_get_tool(bb, d, tool)
    if t:
        import subprocess
        link_path = subprocess.check_output("readlink -f %s" % t, shell=True).decode("utf-8")[:-1]
        if link_path == get_icecc(d):
            bb.error("%s is a symlink to %s in PATH and this prevents icecc from working" % (t, get_icecc(d)))
            return ""
        else:
            return t
    else:
        return t

wait_for_file() {
    local TIME_ELAPSED=0
    local FILE_TO_TEST=$1
    local TIMEOUT=$2
    until [ -f "$FILE_TO_TEST" ]
    do
        TIME_ELAPSED=`expr $TIME_ELAPSED + 1`
        if [ $TIME_ELAPSED -gt $TIMEOUT ]
        then
            return 1
        fi
        sleep 1
    done
}

def set_icecc_env():
    # dummy python version of set_icecc_env
    return

set_icecc_env() {
    if [ "${@use_icecc(bb, d)}" = "no" ]
    then
        return
    fi
    ICECC_VERSION="${@icecc_version(bb, d)}"
    if [ "x${ICECC_VERSION}" = "x" ]
    then
        bbwarn "Cannot use icecc: could not get ICECC_VERSION"
        return
    fi

    ICE_PATH="${@icecc_path(bb, d)}"
    if [ "x${ICE_PATH}" = "x" ]
    then
        bbwarn "Cannot use icecc: could not get ICE_PATH"
        return
    fi

    ICECC_CC="${@icecc_get_and_check_tool(bb, d, "gcc")}"
    ICECC_CXX="${@icecc_get_and_check_tool(bb, d, "g++")}"
    # cannot use icecc_get_and_check_tool here because it assumes as without target_sys prefix
    ICECC_WHICH_AS="${@bb.utils.which(os.getenv('PATH'), 'as')}"
    if [ ! -x "${ICECC_CC}" -o ! -x "${ICECC_CXX}" ]
    then
        bbwarn "Cannot use icecc: could not get ICECC_CC or ICECC_CXX"
        return
    fi

    ICE_VERSION=`$ICECC_CC -dumpversion`
    ICECC_VERSION=`echo ${ICECC_VERSION} | sed -e "s/@VERSION@/$ICE_VERSION/g"`
    if [ ! -x "${ICECC_ENV_EXEC}" ]
    then
        bbwarn "Cannot use icecc: invalid ICECC_ENV_EXEC"
        return
    fi

    ICECC_AS="`${ICECC_CC} -print-prog-name=as`"
    # for target recipes should return something like:
    # /OE/tmp-eglibc/sysroots/x86_64-linux/usr/libexec/arm920tt-oe-linux-gnueabi/gcc/arm-oe-linux-gnueabi/4.8.2/as
    # and just "as" for native, if it returns "as" in current directory (for whatever reason) use "as" from PATH
    if [ "`dirname "${ICECC_AS}"`" = "." ]
    then
        ICECC_AS="${ICECC_WHICH_AS}"
    fi

    if [ ! -f "${ICECC_VERSION}.done" ]
    then
        mkdir -p "`dirname "${ICECC_VERSION}"`"

        # the ICECC_VERSION generation step must be locked by a mutex
        # in order to prevent race conditions
        if flock -n "${ICECC_VERSION}.lock" \
            ${ICECC_ENV_EXEC} "${ICECC_CC}" "${ICECC_CXX}" "${ICECC_AS}" "${ICECC_VERSION}"
        then
            touch "${ICECC_VERSION}.done"
        elif [ ! wait_for_file "${ICECC_VERSION}.done" 30 ]
        then
            # locking failed so wait for ${ICECC_VERSION}.done to appear
            bbwarn "Timeout waiting for ${ICECC_VERSION}.done"
            return
        fi
    fi

    export ICECC_VERSION ICECC_CC ICECC_CXX
    export PATH="$ICE_PATH:$PATH"
    export CCACHE_PATH="$PATH"

    bbnote "Using icecc"
}

do_configure_prepend() {
    set_icecc_env
}

do_compile_prepend() {
    set_icecc_env
}

do_compile_kernelmodules_prepend() {
    set_icecc_env
}

do_install_prepend() {
    set_icecc_env
}
