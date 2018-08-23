SUMMARY = "ICE: Inter-Client Exchange library"

DESCRIPTION = "The Inter-Client Exchange (ICE) protocol provides a \
generic framework for building protocols on top of reliable, byte-stream \
transport connections. It provides basic mechanisms for setting up and \
shutting down connections, for performing authentication, for \
negotiating versions, and for reporting errors. "

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d162b1b3c6fa812da9d804dcf8584a93"

DEPENDS += "xproto xtrans"
PROVIDES = "ice"

PE = "1"

XORG_PN = "libICE"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "addfb1e897ca8079531669c7c7711726"
SRC_URI[sha256sum] = "8f7032f2c1c64352b5423f6b48a8ebdc339cc63064af34d66a6c9aa79759e202"

SRC_URI += "file://CVE-2017-2626.patch"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[arc4] = "ac_cv_lib_bsd_arc4random_buf=yes,ac_cv_lib_bsd_arc4random_buf=no,libbsd"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
