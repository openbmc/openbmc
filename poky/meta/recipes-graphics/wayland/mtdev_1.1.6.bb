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
SRC_URI[md5sum] = "bf8ef2482e84a00b5db8fbd3ce00e249"
SRC_URI[sha256sum] = "15d7b28da8ac71d8bc8c9287c2045fd174267bc740bec10cfda332dc1204e0e0"

inherit autotools pkgconfig
