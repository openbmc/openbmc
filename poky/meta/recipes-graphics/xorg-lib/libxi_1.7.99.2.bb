require xorg-lib-common.inc

SUMMARY = "XI: X Input extension library"

DESCRIPTION = "libxi is an extension to the X11 protocol to support \
input devices other than the core X keyboard and pointer.  It allows \
client programs to select input from these devices independently from \
each other and independently from the core devices."

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=17b064789fab936a1c58c4e13d965b0f \
                    file://src/XIGetDevFocus.c;endline=23;md5=cdfb0d435a33ec57ea0d1e8e395b729f"

DEPENDS += "libxext xorgproto libxfixes"

PE = "1"

XORG_PN = "libXi"

SRC_URI[sha256sum] = "2ce90ebcb7fa0c3ed671924f1268b8f4c500fff62e1284699fc413e182a7896b"

BBCLASSEXTEND = "native nativesdk"
