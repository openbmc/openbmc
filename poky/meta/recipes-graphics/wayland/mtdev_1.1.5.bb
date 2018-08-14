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
SRC_URI[md5sum] = "52c9610b6002f71d1642dc1a1cca5ec1"
SRC_URI[sha256sum] = "6677d5708a7948840de734d8b4675d5980d4561171c5a8e89e54adf7a13eba7f"

inherit autotools pkgconfig
