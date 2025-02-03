SUMMARY = "SM: Session Management library"

DESCRIPTION = "The Session Management Library (SMlib) is a low-level \"C\" \
language interface to XSMP.  The purpose of the X Session Management \
Protocol (XSMP) is to provide a uniform mechanism for users to save and \
restore their sessions.  A session is a group of clients, each of which \
has a particular state."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e04a412a93c7cb2b5e07ebd8fd922917"

DEPENDS += "libice xorgproto xtrans"

# libSM can work without libuuid, we explicitly disable it to break the following circular dependency
# when DISTRO_FEATURES contains 'systemd' and 'x11'.
# systemd -> dbus -> libsm -> util-linux -> systemd
EXTRA_OECONF += "--without-libuuid"

PE = "1"

XORG_PN = "libSM"

SRC_URI[sha256sum] = "2af9e12da5ef670dc3a7bce1895c9c0f1bfb0cb9e64e8db40fcc33f883bd20bc"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

BBCLASSEXTEND = "native nativesdk"
