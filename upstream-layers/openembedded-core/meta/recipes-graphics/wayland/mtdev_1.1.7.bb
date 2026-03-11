SUMMARY = "Multitouch Protocol Translation Library"

DESCRIPTION = "mtdev is a library which transforms all variants of kernel \
multitouch events to the slotted type B protocol. The events put into mtdev may \
be from any MT device, specifically type A without contact tracking, type A with \
contact tracking, or type B with contact tracking"

HOMEPAGE = "http://bitmath.org/code/mtdev/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea6bd0268bb0fcd6b27698616ceee5d6"

SRC_URI = "http://bitmath.org/code/${BPN}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "a107adad2101fecac54ac7f9f0e0a0dd155d954193da55c2340c97f2ff1d814e"

inherit autotools pkgconfig
