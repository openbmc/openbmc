#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# SDK packages are built either explicitly by the user,
# or indirectly via dependency.  No need to be in 'world'.
EXCLUDE_FROM_WORLD = "1"

STAGING_BINDIR_TOOLCHAIN = "${STAGING_DIR_NATIVE}${bindir_native}/${SDK_ARCH}${SDK_VENDOR}-${SDK_OS}"

# libc for the SDK can be different to that of the target
NATIVESDKLIBC ?= "libc-glibc"
LIBCOVERRIDE = ":${NATIVESDKLIBC}"
CLASSOVERRIDE = "class-nativesdk"
MACHINEOVERRIDES = ""

MACHINE_FEATURES = "${SDK_MACHINE_FEATURES}"

MULTILIBS = ""

# we need consistent staging dir whether or not multilib is enabled
STAGING_DIR_HOST = "${WORKDIR}/recipe-sysroot"
STAGING_DIR_TARGET = "${WORKDIR}/recipe-sysroot"
RECIPE_SYSROOT = "${WORKDIR}/recipe-sysroot"

#
# Update PACKAGE_ARCH and PACKAGE_ARCHS
#
PACKAGE_ARCH = "${SDK_ARCH}-${SDKPKGSUFFIX}"
PACKAGE_ARCHS = "${SDK_PACKAGE_ARCHS}"
TUNE_PKGARCH = "${SDK_ARCH}"

#
# We need chrpath >= 0.14 to ensure we can deal with 32 and 64 bit
# binaries
#
DEPENDS:append = " chrpath-replacement-native"
EXTRANATIVEPATH += "chrpath-native"

PKGDATA_DIR = "${PKGDATA_DIR_SDK}"

HOST_ARCH = "${SDK_ARCH}"
HOST_VENDOR = "${SDK_VENDOR}"
HOST_OS = "${SDK_OS}"
HOST_PREFIX = "${SDK_PREFIX}"
HOST_CC_ARCH = "${SDK_CC_ARCH}"
HOST_LD_ARCH = "${SDK_LD_ARCH}"
HOST_AS_ARCH = "${SDK_AS_ARCH}"
#HOST_SYS = "${HOST_ARCH}${TARGET_VENDOR}-${HOST_OS}"

TARGET_ARCH = "${SDK_ARCH}"
TARGET_VENDOR = "${SDK_VENDOR}"
TARGET_OS = "${SDK_OS}"
TARGET_PREFIX = "${SDK_PREFIX}"
TARGET_CC_ARCH = "${SDK_CC_ARCH}"
TARGET_LD_ARCH = "${SDK_LD_ARCH}"
TARGET_AS_ARCH = "${SDK_AS_ARCH}"
TARGET_CPPFLAGS = "${BUILDSDK_CPPFLAGS}"
TARGET_CFLAGS = "${BUILDSDK_CFLAGS}"
TARGET_CXXFLAGS = "${BUILDSDK_CXXFLAGS}"
TARGET_LDFLAGS = "${BUILDSDK_LDFLAGS}"
TARGET_FPU = ""
EXTRA_OECONF_GCC_FLOAT = ""
TUNE_FEATURES = ""

# Change to place files in SDKPATH
base_prefix = "${SDKPATHNATIVE}"
prefix = "${SDKPATHNATIVE}${prefix_nativesdk}"
exec_prefix = "${SDKPATHNATIVE}${prefix_nativesdk}"
baselib = "lib"
sbindir = "${bindir}"

python nativesdk_virtclass_handler () {
    pn = e.data.getVar("PN")
    if not (pn.endswith("-nativesdk") or pn.startswith("nativesdk-")):
        return

    # Set features here to prevent DISTRO_FEATURES modifications from affecting
    # nativesdk distro features
    features = set(d.getVar("DISTRO_FEATURES_NATIVESDK").split())
    oe.utils.filter_default_features("DISTRO_FEATURES", d)
    filtered = set(bb.utils.filter("DISTRO_FEATURES", d.getVar("DISTRO_FEATURES_FILTER_NATIVESDK"), d).split())
    d.setVar("DISTRO_FEATURES", " ".join(sorted(features | filtered)))
    d.setVar("DISTRO_FEATURES_DEFAULTS", "")

    e.data.setVar("MLPREFIX", "nativesdk-")
    e.data.setVar("PN", "nativesdk-" + e.data.getVar("PN").replace("-nativesdk", "").replace("nativesdk-", ""))
}

python () {
    pn = d.getVar("PN")
    if not pn.startswith("nativesdk-"):
        return

    import oe.classextend

    clsextend = oe.classextend.ClassExtender("nativesdk", [], d)
    clsextend.rename_package_variables((d.getVar("PACKAGEVARS") or "").split())

    clsextend.set_filter("DEPENDS", deps=True)
    clsextend.set_filter("PACKAGE_WRITE_DEPS", deps=False)
    clsextend.set_filter("PROVIDES", deps=False)

    if "nativesdk" in (d.getVar("BBCLASSEXTEND") or ""):
        clsextend.map_packagevars()

    d.setVar("LIBCEXTENSION", "")
    d.setVar("ABIEXTENSION", "")
}

addhandler nativesdk_virtclass_handler
nativesdk_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"

do_packagedata[stamp-extra-info] = ""

USE_NLS = "${SDKUSE_NLS}"

OLDEST_KERNEL = "${SDK_OLDEST_KERNEL}"

PATH:prepend = "${COREBASE}/scripts/nativesdk-intercept:"
