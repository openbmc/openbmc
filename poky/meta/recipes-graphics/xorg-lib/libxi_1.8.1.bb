require xorg-lib-common.inc

SUMMARY = "XI: X Input extension library"

DESCRIPTION = "libxi is an extension to the X11 protocol to support \
input devices other than the core X keyboard and pointer.  It allows \
client programs to select input from these devices independently from \
each other and independently from the core devices."

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=17b064789fab936a1c58c4e13d965b0f \
                    file://src/XIGetDevFocus.c;endline=23;md5=cdfb0d435a33ec57ea0d1e8e395b729f"

DEPENDS += "libxext xorgproto libxfixes"

PE = "1"

XORG_PN = "libXi"
XORG_EXT = "tar.xz"

SRC_URI[sha256sum] = "89bfc0e814f288f784202e6e5f9b362b788ccecdeb078670145eacd8749656a7"

BBCLASSEXTEND = "native nativesdk"
