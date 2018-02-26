SUMMARY = "Xt: X Toolkit Intrinsics library"

DESCRIPTION = "The Intrinsics are a programming library tailored to the \
special requirements of user interface construction within a network \
window system, specifically the X Window System. The Intrinsics and a \
widget set make up an X Toolkit. The Intrinsics provide the base \
mechanism necessary to build a wide variety of interoperating widget \
sets and application environments. The Intrinsics are a layer on top of \
Xlib, the C Library X Interface. They extend the fundamental \
abstractions provided by the X Window System while still remaining \
independent of any particular user interface policy or style."

require xorg-lib-common.inc

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=6565b1e0094ea1caae0971cc4035f343"


DEPENDS += "util-linux libxcb libsm virtual/libx11 kbproto libxdmcp"
PROVIDES = "xt"

PE = "1"

XORG_PN = "libXt"

SRC_URI +=  "file://libxt_fix_for_x32.patch \
             file://0001-libXt-util-don-t-link-makestrs-with-target-cflags.patch \
            "

BBCLASSEXTEND = "native"

EXTRA_OECONF += "--disable-xkb"

SRC_URI[md5sum] = "8f5b5576fbabba29a05f3ca2226f74d3"
SRC_URI[sha256sum] = "46eeb6be780211fdd98c5109286618f6707712235fdd19df4ce1e6954f349f1a"
