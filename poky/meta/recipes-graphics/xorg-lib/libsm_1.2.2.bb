SUMMARY = "SM: Session Management library"

DESCRIPTION = "The Session Management Library (SMlib) is a low-level \"C\" \
language interface to XSMP.  The purpose of the X Session Management \
Protocol (XSMP) is to provide a uniform mechanism for users to save and \
restore their sessions.  A session is a group of clients, each of which \
has a particular state."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0fb37f44e02bdbde80546024400728d"

DEPENDS += "libice xorgproto xtrans"

# libSM can work without libuuid, we explicitly disable it to break the following circular dependency
# when DISTRO_FEATURES contains 'systemd' and 'x11'.
# systemd -> dbus -> libsm -> util-linux -> systemd
EXTRA_OECONF += "--without-libuuid"

PE = "1"

XORG_PN = "libSM"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "499a7773c65aba513609fe651853c5f3"
SRC_URI[sha256sum] = "0baca8c9f5d934450a70896c4ad38d06475521255ca63b717a6510fdb6e287bd"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
