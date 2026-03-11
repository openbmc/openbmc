DESCRIPTION = "An implementation of the most relevant parts of IEC 61883 over \
libraw1394 for transmission, reception, and management of media streams and \
devices such as DV, MPEG2-TS, audio, and MIDI"
HOMEPAGE = "https://ieee1394.wiki.kernel.org/index.php/Libraries#libiec61883"
SECTION = "libs/multimedia"

DEPENDS = "libraw1394"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=771782cb6245c7fbbe74bc0ec059beff"

SRC_URI = "https://www.kernel.org/pub/linux/libs/ieee1394/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "ed91bc1727fac8e019402fc3724a283d"
SRC_URI[sha256sum] = "d1e02c7e276fac37313a2f8c1c33d7a9e19282ff16f32e72435428ff5121f09e"

inherit autotools pkgconfig
