SUMMARY = "MMS stream protocol library"
HOMEPAGE = "http://sourceforge.net/projects/libmms/"
SECTION = "libs/multimedia"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=fad9b3332be894bab9bc501572864b29"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "d6b665b335a6360e000976e770da7691"
SRC_URI[sha256sum] = "3c05e05aebcbfcc044d9e8c2d4646cd8359be39a3f0ba8ce4e72a9094bee704f"

inherit autotools pkgconfig
