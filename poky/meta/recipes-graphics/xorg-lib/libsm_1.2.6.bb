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

SRC_URI[sha256sum] = "be7c0abdb15cbfd29ac62573c1c82e877f9d4047ad15321e7ea97d1e43d835be"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

BBCLASSEXTEND = "native nativesdk"
