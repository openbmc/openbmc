SUMMARY = "ICE: Inter-Client Exchange library"

DESCRIPTION = "The Inter-Client Exchange (ICE) protocol provides a \
generic framework for building protocols on top of reliable, byte-stream \
transport connections. It provides basic mechanisms for setting up and \
shutting down connections, for performing authentication, for \
negotiating versions, and for reporting errors. "

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d162b1b3c6fa812da9d804dcf8584a93"

DEPENDS += "xorgproto xtrans"
PROVIDES = "ice"

PE = "1"

XORG_PN = "libICE"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "974e4ed414225eb3c716985df9709f4da8d22a67a2890066bc6dfc89ad298625"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[arc4] = "ac_cv_lib_bsd_arc4random_buf=yes,ac_cv_lib_bsd_arc4random_buf=no,libbsd"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
