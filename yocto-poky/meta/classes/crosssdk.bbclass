inherit cross

CLASSOVERRIDE = "class-crosssdk"
MACHINEOVERRIDES = ""
PACKAGE_ARCH = "${SDK_ARCH}"
python () {
	# set TUNE_PKGARCH to SDK_ARCH
	d.setVar('TUNE_PKGARCH', d.getVar('SDK_ARCH', True))
}

STAGING_DIR_TARGET = "${STAGING_DIR}/${SDK_ARCH}-${SDKPKGSUFFIX}${SDK_VENDOR}-${SDK_OS}"
STAGING_BINDIR_TOOLCHAIN = "${STAGING_DIR_NATIVE}${bindir_native}/${TARGET_ARCH}${TARGET_VENDOR}-${TARGET_OS}"

TARGET_ARCH = "${SDK_ARCH}"
TARGET_VENDOR = "${SDK_VENDOR}"
TARGET_OS = "${SDK_OS}"
TARGET_PREFIX = "${SDK_PREFIX}"
TARGET_CC_ARCH = "${SDK_CC_ARCH}"
TARGET_LD_ARCH = "${SDK_LD_ARCH}"
TARGET_AS_ARCH = "${SDK_AS_ARCH}"
TARGET_FPU = ""

target_libdir = "${SDKPATHNATIVE}${libdir_nativesdk}"
target_includedir = "${SDKPATHNATIVE}${includedir_nativesdk}"
target_base_libdir = "${SDKPATHNATIVE}${base_libdir_nativesdk}"
target_prefix = "${SDKPATHNATIVE}${prefix_nativesdk}"
target_exec_prefix = "${SDKPATHNATIVE}${prefix_nativesdk}"
baselib = "lib"

do_populate_sysroot[stamp-extra-info] = ""
do_packagedata[stamp-extra-info] = ""

# Need to force this to ensure consitency accross architectures
EXTRA_OECONF_GCC_FLOAT = ""

USE_NLS = "no"
