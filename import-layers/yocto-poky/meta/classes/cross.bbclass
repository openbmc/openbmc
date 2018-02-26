inherit relocatable

# Cross packages are built indirectly via dependency,
# no need for them to be a direct target of 'world'
EXCLUDE_FROM_WORLD = "1"

CLASSOVERRIDE = "class-cross"
PACKAGES = ""
PACKAGES_DYNAMIC = ""
PACKAGES_DYNAMIC_class-native = ""

HOST_ARCH = "${BUILD_ARCH}"
HOST_VENDOR = "${BUILD_VENDOR}"
HOST_OS = "${BUILD_OS}"
HOST_PREFIX = "${BUILD_PREFIX}"
HOST_CC_ARCH = "${BUILD_CC_ARCH}"
HOST_LD_ARCH = "${BUILD_LD_ARCH}"
HOST_AS_ARCH = "${BUILD_AS_ARCH}"

export lt_cv_sys_lib_dlsearch_path_spec = "${libdir} ${base_libdir} /lib /lib64 /usr/lib /usr/lib64"

STAGING_DIR_HOST = "${RECIPE_SYSROOT_NATIVE}"

PACKAGE_ARCH = "${BUILD_ARCH}"

MULTIMACH_TARGET_SYS = "${BUILD_ARCH}${BUILD_VENDOR}-${BUILD_OS}"

export PKG_CONFIG_DIR = "${exec_prefix}/lib/pkgconfig"
export PKG_CONFIG_SYSROOT_DIR = ""

TARGET_CPPFLAGS = ""
TARGET_CFLAGS = ""
TARGET_CXXFLAGS = ""
TARGET_LDFLAGS = ""

CPPFLAGS = "${BUILD_CPPFLAGS}"
CFLAGS = "${BUILD_CFLAGS}"
CXXFLAGS = "${BUILD_CFLAGS}"
LDFLAGS = "${BUILD_LDFLAGS}"
LDFLAGS_build-darwin = "-L${STAGING_LIBDIR_NATIVE}"

TOOLCHAIN_OPTIONS = ""

DEPENDS_GETTEXT = "gettext-native"

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
do_populate_sysroot[stamp-extra-info] = ""
do_packagedata[stamp-extra-info] = ""

do_install () {
	oe_runmake 'DESTDIR=${D}' install
}

USE_NLS = "no"

export CC = "${BUILD_CC}"
export CXX = "${BUILD_CXX}"
export FC = "${BUILD_FC}"
export CPP = "${BUILD_CPP}"
export LD = "${BUILD_LD}"
export CCLD = "${BUILD_CCLD}"
export AR = "${BUILD_AR}"
export AS = "${BUILD_AS}"
export RANLIB = "${BUILD_RANLIB}"
export STRIP = "${BUILD_STRIP}"
export NM = "${BUILD_NM}"

inherit nopackages
