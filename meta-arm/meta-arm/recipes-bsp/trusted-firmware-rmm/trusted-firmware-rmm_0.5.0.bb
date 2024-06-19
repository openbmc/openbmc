SUMMARY = "RMM Firmware"
DESCRIPTION = "RMM Firmware for Arm reference platforms"
LICENSE = "BSD-3-Clause & MIT"

SRC_URI = "gitsm://git.trustedfirmware.org/TF-RMM/tf-rmm.git;protocol=https;branch=main \
          "

SRCREV = "6184a730bd4bc80d59eeff7f0752f8423500202c"
UPSTREAM_CHECK_GITTAGREGEX = "^tf-rmm-v(?P<pver>\d+(\.\d+)+)$"

LIC_FILES_CHKSUM += "file://docs/about/license.rst;md5=1375c7c641558198ffe401c2a799d79b"

inherit deploy cmake

RMM_CONFIG ?= ""
RMM_CONFIG:qemuarm64 = "qemu_virt_defcfg"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "qemuarm64"

S = "${WORKDIR}/git"

# Build for debug (set RMM_DEBUG to 1 to activate)
RMM_DEBUG ?= "0"
RMM_BUILD_MODE ?= "${@bb.utils.contains('RMM_DEBUG', '1', 'Debug', 'Release', d)}"

# Handle RMM_DEBUG parameter
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=${RMM_BUILD_MODE}"
EXTRA_OECMAKE += "-DRMM_CONFIG=${RMM_CONFIG}"

# Supplement include path
EXTRA_OECMAKE += "-DCMAKE_INCLUDE_PATH=${STAGING_INCDIR}"

# When compiling for Aarch64 on non-native hosts, the RMM code base gets its
# toolchain from CROSS_COMPILE rather than CMAKE_TOOLCHAIN_FILE
export CROSS_COMPILE="${TARGET_PREFIX}"

do_install() {
    install -d -m 755 ${D}/firmware
    install -m 0644 ${B}/${RMM_BUILD_MODE}/* ${D}/firmware/
}

FILES:${PN} = "/firmware"
SYSROOT_DIRS += "/firmware"

do_deploy() {
    cp -rf ${D}/firmware/* ${DEPLOYDIR}/
}

addtask deploy after do_install
