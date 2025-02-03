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
LIC_FILES_CHKSUM = "file://COPYING;md5=d6e9ca2c4b2276625afe025b0a2a4d8c"

DEPENDS += "util-linux libxcb libsm virtual/libx11 xorgproto libxdmcp"
PROVIDES = "xt"

PE = "1"

XORG_PN = "libXt"
XORG_EXT = "tar.xz"

SRC_URI += "file://libxt_fix_for_x32.patch"

SRC_URI[sha256sum] = "e0a774b33324f4d4c05b199ea45050f87206586d81655f8bef4dba434d931288"

BBCLASSEXTEND = "native nativesdk"

EXTRA_OECONF += "--disable-xkb"
