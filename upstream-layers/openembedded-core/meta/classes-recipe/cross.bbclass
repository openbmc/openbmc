#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit relocatable

# Cross packages are built indirectly via dependency,
# no need for them to be a direct target of 'world'
EXCLUDE_FROM_WORLD = "1"

CLASSOVERRIDE = "class-cross"
PACKAGES = ""
PACKAGES_DYNAMIC = ""
PACKAGES_DYNAMIC:class-native = ""

HOST_ARCH = "${BUILD_ARCH}"
HOST_VENDOR = "${BUILD_VENDOR}"
HOST_OS = "${BUILD_OS}"
HOST_PREFIX = "${BUILD_PREFIX}"
HOST_CC_ARCH = "${BUILD_CC_ARCH}"
HOST_LD_ARCH = "${BUILD_LD_ARCH}"
HOST_AS_ARCH = "${BUILD_AS_ARCH}"

INHIBIT_SYSROOT_STRIP ??= ""

export lt_cv_sys_lib_dlsearch_path_spec = "${libdir} ${base_libdir} /lib /lib64 /usr/lib /usr/lib64"

STAGING_DIR_HOST = "${RECIPE_SYSROOT_NATIVE}"

PACKAGE_ARCH = "${BUILD_ARCH}"

MULTIMACH_TARGET_SYS = "${BUILD_ARCH}${BUILD_VENDOR}-${BUILD_OS}"

PKG_CONFIG_LIBDIR = "${exec_prefix}/lib/pkgconfig:${exec_prefix}/share/pkgconfig"
PKG_CONFIG_SYSROOT_DIR = ""

TARGET_CPPFLAGS = ""
TARGET_CFLAGS = ""
TARGET_CXXFLAGS = ""
TARGET_LDFLAGS = ""

CPPFLAGS = "${BUILD_CPPFLAGS}"
CFLAGS = "${BUILD_CFLAGS}"
CXXFLAGS = "${BUILD_CFLAGS}"
LDFLAGS = "${BUILD_LDFLAGS}"

TOOLCHAIN_OPTIONS = ""

# This class encodes staging paths into its scripts data so can only be
# reused if we manipulate the paths.
SSTATE_SCAN_CMD ?= "${SSTATE_SCAN_CMD_NATIVE}"

# Path mangling needed by the cross packaging
# Note that we use := here to ensure that libdir and includedir are
# target paths.
target_base_prefix := "${root_prefix}"
target_prefix := "${prefix}"
target_exec_prefix := "${exec_prefix}"
target_base_libdir = "${target_base_prefix}/${baselib}"
target_libdir = "${target_exec_prefix}/${baselib}"
target_includedir := "${includedir}"

# Overrides for paths
CROSS_TARGET_SYS_DIR = "${TARGET_SYS}"
prefix = "${STAGING_DIR_NATIVE}${prefix_native}"
base_prefix = "${STAGING_DIR_NATIVE}"
exec_prefix = "${STAGING_DIR_NATIVE}${prefix_native}"
bindir = "${exec_prefix}/bin/${CROSS_TARGET_SYS_DIR}"
sbindir = "${bindir}"
base_bindir = "${bindir}"
base_sbindir = "${bindir}"
libdir = "${exec_prefix}/lib/${CROSS_TARGET_SYS_DIR}"
libexecdir = "${exec_prefix}/libexec/${CROSS_TARGET_SYS_DIR}"

do_populate_sysroot[sstate-inputdirs] = "${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/"
do_packagedata[stamp-extra-info] = ""

USE_NLS = "no"

CC = "${BUILD_CC}"
CXX = "${BUILD_CXX}"
FC = "${BUILD_FC}"
CPP = "${BUILD_CPP}"
LD = "${BUILD_LD}"
CCLD = "${BUILD_CCLD}"
AR = "${BUILD_AR}"
AS = "${BUILD_AS}"
RANLIB = "${BUILD_RANLIB}"
STRIP = "${BUILD_STRIP}"
NM = "${BUILD_NM}"

inherit nopackages

python do_addto_recipe_sysroot () {
    bb.build.exec_func("extend_recipe_sysroot", d)
}
addtask addto_recipe_sysroot after do_populate_sysroot
do_addto_recipe_sysroot[deptask] = "do_populate_sysroot"

PATH:prepend = "${COREBASE}/scripts/cross-intercept:"

#
# Cross task outputs can call native dependencies and even when cross
# recipe output doesn't change it might produce different results when
# the called native dependency is changed, e.g. clang-cross-${TARGET_ARCH}
# contains symlink to clang binary from clang-native, but when clang-native
# outhash is changed, clang-cross-${TARGET_ARCH} will still be considered
# equivalent and target recipes aren't rebuilt with new clang binary, see
# work around in https://github.com/kraj/meta-clang/pull/1140 to make target
# recipes to depend directly not only on clang-cross-${TARGET_ARCH} but
# clang-native as well.
#
# This can cause poor interactions with hash equivalence, since this recipes
# output-changing dependency is "hidden" and downstream task only see that this
# recipe has the same outhash and therefore is equivalent. This can result in
# different output in different cases.
#
# To resolve this, unhide the output-changing dependency by adding its unihash
# to this tasks outhash calculation. Unfortunately, don't know specifically
# know which dependencies are output-changing, so we have to add all of them.
#
python cross_add_do_populate_sysroot_deps () {
    current_task = "do_" + d.getVar("BB_CURRENTTASK")
    if current_task != "do_populate_sysroot":
        return

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    pn = d.getVar("PN")
    deps = {
        dep[0]:dep[6] for dep in taskdepdata.values() if
            dep[1] == current_task and dep[0] != pn
    }

    d.setVar("HASHEQUIV_EXTRA_SIGDATA", "\n".join("%s: %s" % (k, deps[k]) for k in sorted(deps.keys())))
}
SSTATECREATEFUNCS += "cross_add_do_populate_sysroot_deps"
