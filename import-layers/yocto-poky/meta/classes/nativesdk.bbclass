# SDK packages are built either explicitly by the user,
# or indirectly via dependency.  No need to be in 'world'.
EXCLUDE_FROM_WORLD = "1"

STAGING_BINDIR_TOOLCHAIN = "${STAGING_DIR_NATIVE}${bindir_native}/${SDK_ARCH}${SDK_VENDOR}-${SDK_OS}"

# libc for the SDK can be different to that of the target
NATIVESDKLIBC ?= "libc-glibc"
LIBCOVERRIDE = ":${NATIVESDKLIBC}"
CLASSOVERRIDE = "class-nativesdk"
MACHINEOVERRIDES = ""

#
# Update PACKAGE_ARCH and PACKAGE_ARCHS
#
PACKAGE_ARCH = "${SDK_ARCH}-${SDKPKGSUFFIX}"
PACKAGE_ARCHS = "${SDK_PACKAGE_ARCHS}"

#
# We need chrpath >= 0.14 to ensure we can deal with 32 and 64 bit
# binaries
#
DEPENDS_append = " chrpath-replacement-native"
EXTRANATIVEPATH += "chrpath-native"

STAGING_DIR_HOST = "${STAGING_DIR}/${MULTIMACH_HOST_SYS}"
STAGING_DIR_TARGET = "${STAGING_DIR}/${MULTIMACH_TARGET_SYS}"
PKGDATA_DIR = "${STAGING_DIR_HOST}/pkgdata"

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
TARGET_FPU = ""
EXTRA_OECONF_GCC_FLOAT = ""

CPPFLAGS = "${BUILDSDK_CPPFLAGS}"
CFLAGS = "${BUILDSDK_CFLAGS}"
CXXFLAGS = "${BUILDSDK_CFLAGS}"
LDFLAGS = "${BUILDSDK_LDFLAGS}"

# Change to place files in SDKPATH
base_prefix = "${SDKPATHNATIVE}"
prefix = "${SDKPATHNATIVE}${prefix_nativesdk}"
exec_prefix = "${SDKPATHNATIVE}${prefix_nativesdk}"
baselib = "lib"
sbindir = "${bindir}"

export PKG_CONFIG_DIR = "${STAGING_DIR_HOST}${libdir}/pkgconfig"
export PKG_CONFIG_SYSROOT_DIR = "${STAGING_DIR_HOST}"

python nativesdk_virtclass_handler () {
    pn = e.data.getVar("PN", True)
    if not (pn.endswith("-nativesdk") or pn.startswith("nativesdk-")):
        return

    e.data.setVar("MLPREFIX", "nativesdk-")
    e.data.setVar("PN", "nativesdk-" + e.data.getVar("PN", True).replace("-nativesdk", "").replace("nativesdk-", ""))
    e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + ":virtclass-nativesdk")
}

python () {
    pn = d.getVar("PN", True)
    if not pn.startswith("nativesdk-"):
        return

    import oe.classextend

    clsextend = oe.classextend.NativesdkClassExtender("nativesdk", d)
    clsextend.rename_packages()
    clsextend.rename_package_variables((d.getVar("PACKAGEVARS", True) or "").split())

    clsextend.map_depends_variable("DEPENDS")
    clsextend.map_packagevars()
    clsextend.map_variable("PROVIDES")
    clsextend.map_regexp_variable("PACKAGES_DYNAMIC")
}

addhandler nativesdk_virtclass_handler
nativesdk_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"

do_populate_sysroot[stamp-extra-info] = ""
do_packagedata[stamp-extra-info] = ""

USE_NLS = "${SDKUSE_NLS}"
