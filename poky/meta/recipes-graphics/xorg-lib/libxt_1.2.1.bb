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

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=73d55cea4d27ca1a09a5d23378b3ecf8"

DEPENDS += "util-linux libxcb libsm virtual/libx11 xorgproto libxdmcp"
PROVIDES = "xt"

PE = "1"

XORG_PN = "libXt"

SRC_URI += "file://libxt_fix_for_x32.patch"

SRC_URI[sha256sum] = "679cc08f1646dbd27f5e48ffe8dd49406102937109130caab02ca32c083a3d60"

BBCLASSEXTEND = "native nativesdk"

EXTRA_OECONF += "--disable-xkb"
