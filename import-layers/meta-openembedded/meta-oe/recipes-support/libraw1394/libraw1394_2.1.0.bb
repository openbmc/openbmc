SUMMARY = "base library for low-level IEEE 1394 accesses"
HOMEPAGE = "https://ieee1394.wiki.kernel.org/index.php/Libraries#libraw1394"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "https://www.kernel.org/pub/linux/libs/ieee1394/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "d06cccb776b240b6ab5efdee33b87af2"
SRC_URI[sha256sum] = "a83cff16fb8885831bc29d7d17f3c570dc39251d89e20795c08e87720de0ba70"

inherit autotools
