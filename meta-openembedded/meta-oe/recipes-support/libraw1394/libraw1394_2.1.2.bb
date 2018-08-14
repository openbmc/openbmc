SUMMARY = "base library for low-level IEEE 1394 accesses"
HOMEPAGE = "https://ieee1394.wiki.kernel.org/index.php/Libraries#libraw1394"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "https://www.kernel.org/pub/linux/libs/ieee1394/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "45031ab15ca93e50c19886a38bc1814a"
SRC_URI[sha256sum] = "ddc4e32721cdfe680d964aaede68ac606a20cd17dd2ba70e2d7e0692086ab57c"

inherit autotools
