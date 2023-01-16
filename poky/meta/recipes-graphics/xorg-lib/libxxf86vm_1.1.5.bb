SUMMARY = "XFree86-VM: XFree86 video mode extension library"

DESCRIPTION = "libXxf86vm provides an interface to the \
XFree86-VidModeExtension extension, which allows client applications to \
get and set video mode timings in extensive detail.  It is used by the \
xvidtune program in particular."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fa0b9c462d8f2f13eba26492d42ea63d"

DEPENDS += "libxext xorgproto"

PE = "1"

XORG_PN = "libXxf86vm"
SRC_URI[sha256sum] = "247fef48b3e0e7e67129e41f1e789e8d006ba47dba1c0cdce684b9b703f888e7"

BBCLASSEXTEND = "native nativesdk"
