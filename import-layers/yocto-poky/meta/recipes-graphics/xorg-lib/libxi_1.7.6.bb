require xorg-lib-common.inc

SUMMARY = "XI: X Input extension library"

DESCRIPTION = "libxi is an extension to the X11 protocol to support \
input devices other than the core X keyboard and pointer.  It allows \
client programs to select input from these devices independently from \
each other and independently from the core devices."

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=17b064789fab936a1c58c4e13d965b0f \
                    file://src/XIGetDevFocus.c;endline=23;md5=cdfb0d435a33ec57ea0d1e8e395b729f"

DEPENDS += "libxext inputproto libxfixes"

PE = "1"

XORG_PN = "libXi"

SRC_URI[md5sum] = "510e555ecfffa8d2298a0f42b725e563"
SRC_URI[sha256sum] = "1f32a552cec0f056c0260bdb32e853cec0673d2f40646ce932ad5a9f0205b7ac"
