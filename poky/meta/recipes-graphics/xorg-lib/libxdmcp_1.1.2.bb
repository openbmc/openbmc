SUMMARY = "XDMCP: X Display Manager Control Protocol library"

DESCRIPTION = "The purpose of the X Display Manager Control Protocol \
(XDMCP) is to provide a uniform mechanism for an autonomous display to \
request login service from a remote host. An X terminal (screen, \
keyboard, mouse, processor, network interface) is a prime example of an \
autonomous display."

require xorg-lib-common.inc

inherit gettext

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d559fb26e129626022e052a5e6e0e123"

DEPENDS += "xproto"
PROVIDES = "xdmcp"

PE = "1"

XORG_PN = "libXdmcp"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "18aa5c1279b01f9d18e3299969665b2e"
SRC_URI[sha256sum] = "81fe09867918fff258296e1e1e159f0dc639cb30d201c53519f25ab73af4e4e2"

PACKAGECONFIG ??= ""
PACKAGECONFIG[arc4] = "ac_cv_lib_bsd_arc4random_buf=yes,ac_cv_lib_bsd_arc4random_buf=no,libbsd"
