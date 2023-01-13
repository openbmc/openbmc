SUMMARY = "SM: Session Management library"

DESCRIPTION = "The Session Management Library (SMlib) is a low-level \"C\" \
language interface to XSMP.  The purpose of the X Session Management \
Protocol (XSMP) is to provide a uniform mechanism for users to save and \
restore their sessions.  A session is a group of clients, each of which \
has a particular state."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0fb37f44e02bdbde80546024400728d"

DEPENDS += "libice xorgproto xtrans"

# libSM can work without libuuid, we explicitly disable it to break the following circular dependency
# when DISTRO_FEATURES contains 'systemd' and 'x11'.
# systemd -> dbus -> libsm -> util-linux -> systemd
EXTRA_OECONF += "--without-libuuid"

PE = "1"

XORG_PN = "libSM"

SRC_URI[sha256sum] = "fdcbe51e4d1276b1183da77a8a4e74a137ca203e0bcfb20972dd5f3347e97b84"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

BBCLASSEXTEND = "native nativesdk"
