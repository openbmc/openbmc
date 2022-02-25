SUMMARY = "XDMCP: X Display Manager Control Protocol library"

DESCRIPTION = "The purpose of the X Display Manager Control Protocol \
(XDMCP) is to provide a uniform mechanism for an autonomous display to \
request login service from a remote host. An X terminal (screen, \
keyboard, mouse, processor, network interface) is a prime example of an \
autonomous display."

require xorg-lib-common.inc

inherit gettext

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d559fb26e129626022e052a5e6e0e123"

DEPENDS += "xorgproto"
PROVIDES = "xdmcp"

PE = "1"

XORG_PN = "libXdmcp"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "115c5c12ecce0e749cd91d999a5fd160"
SRC_URI[sha256sum] = "20523b44aaa513e17c009e873ad7bbc301507a3224c232610ce2e099011c6529"

PACKAGECONFIG ??= ""
PACKAGECONFIG[arc4] = "ac_cv_lib_bsd_arc4random_buf=yes,ac_cv_lib_bsd_arc4random_buf=no,libbsd"
