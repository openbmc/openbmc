SUMMARY = "ZNC, an advanced IRC bouncer"
SECTION = "net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl zlib icu"

SRC_URI = "gitsm://github.com/znc/znc.git;branch=master;protocol=https"

SRCREV = "bf253640d33d03331310778e001fb6f5aba2989e"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

# ZNC has a custom autogen.sh that states that this command is needed *and* expected to fail
do_configure:prepend() {
    automake --add-missing || true
}

do_install:append() {
    sed -i -e 's|${DEBUG_PREFIX_MAP}||g; s|--sysroot=${STAGING_DIR_TARGET}||g' ${D}${libdir}/pkgconfig/*.pc
    sed -i -e 's|${DEBUG_PREFIX_MAP}||g; s|--sysroot=${STAGING_DIR_TARGET}||g' ${D}${bindir}/znc-buildmod
}
