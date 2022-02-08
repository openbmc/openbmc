SUMMARY = "ZNC, an advanced IRC bouncer"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl zlib icu"

SRC_URI = "git://github.com/znc/znc.git;name=znc;branch=master;protocol=https \
           git://github.com/jimloco/Csocket.git;destsuffix=git/third_party/Csocket;name=Csocket;branch=master;protocol=https \
          "
SRCREV_znc = "c7f72f8bc800115ac985e7e13eace78031cb1b50"
SRCREV_Csocket = "e8d9e0bb248c521c2c7fa01e1c6a116d929c41b4"

# This constructs a composite revision based on multiple SRCREV's.
#
SRCREV_FORMAT = "znc_Csocket"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

# ZNC has a custom autogen.sh that states that this command is needed *and* expected to fail
do_configure_prepend() {
    automake --add-missing || true
}

do_install_append() {
    sed -i -e 's|${DEBUG_PREFIX_MAP}||g; s|--sysroot=${STAGING_DIR_TARGET}||g' ${D}${libdir}/pkgconfig/*.pc
}
