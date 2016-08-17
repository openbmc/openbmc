SUMMARY = "XFree86-VM: XFree86 video mode extension library"

DESCRIPTION = "libXxf86vm provides an interface to the \
XFree86-VidModeExtension extension, which allows client applications to \
get and set video mode timings in extensive detail.  It is used by the \
xvidtune program in particular."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fa0b9c462d8f2f13eba26492d42ea63d"

DEPENDS += "libxext xf86vidmodeproto"

PE = "1"

XORG_PN = "libXxf86vm"

SRC_URI[md5sum] = "298b8fff82df17304dfdb5fe4066fe3a"
SRC_URI[sha256sum] = "afee27f93c5f31c0ad582852c0fb36d50e4de7cd585fcf655e278a633d85cd57"
