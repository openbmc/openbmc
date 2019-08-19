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

SRC_URI[md5sum] = "62c4af0839072024b4b1c8cbe84216c7"
SRC_URI[sha256sum] = "36a30d8f6383a72e7ce060298b4b181fd298bc3a135c8e201b7ca847f5f81061"

BBCLASSEXTEND = "native nativesdk"
