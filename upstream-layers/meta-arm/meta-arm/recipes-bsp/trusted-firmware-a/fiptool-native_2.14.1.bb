# Firmware Image Package (FIP)
# It is a packaging format used by TF-A to package the
# firmware images in a single binary.

DESCRIPTION = "fiptool - Trusted Firmware tool for packaging"
LICENSE = "BSD-3-Clause"

SRC_URI_TRUSTED_FIRMWARE_A ?= "git://review.trustedfirmware.org/TF-A/trusted-firmware-a;protocol=https"
SRC_URI = "${SRC_URI_TRUSTED_FIRMWARE_A};destsuffix=fiptool-${PV};branch=${SRCBRANCH}"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"

# Use fiptool from TF-A v2.14.1
SRCREV = "e82c7ced9e76aea35b176e608d67dfe5ebe1c569"
SRCBRANCH = "lts-v2.14"

DEPENDS += "openssl-native"

inherit native

EXTRA_OEMAKE = "V=1 HOSTCC='${BUILD_CC}' OPENSSL_DIR=${STAGING_DIR_NATIVE}/${prefix_native}"

do_compile () {
    # This is still needed to have the native fiptool executing properly by
    # setting the RPATH
    sed -i '/^LDOPTS/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^INCLUDE_PATHS/ s,$, \$\{BUILD_CFLAGS},' ${S}/tools/fiptool/Makefile

    oe_runmake fiptool
}

do_install () {
    install -D -p -m 0755 tools/fiptool/fiptool ${D}${bindir}/fiptool
}
