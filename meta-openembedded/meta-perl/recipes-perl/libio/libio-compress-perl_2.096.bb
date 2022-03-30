DESCRIPTION = "This module is not intended for direct use in application code. Its sole \
purpose is to be sub-classed by IO::Compress modules."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

MAINTAINER=	"Poky <poky@yoctoproject.org>"
HOMEPAGE=	"https://metacpan.org/release/IO-Compress"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d"

SRC_URI = "https://cpan.metacpan.org/authors/id/P/PM/PMQS/IO-Compress-2.096.tar.gz"

SRC_URI[md5sum] = "18ad197cad5ca87bc3a7d2538998e017"
SRC_URI[sha256sum] = "9d219fd5df4b490b5d2f847921e3cb1c3392758fa0bae9b05a8992b3620ba572"
RDEPENDS:${PN} += "compress-raw-bzip2-perl"
RDEPENDS:${PN} += "compress-raw-zlib-perl"

S = "${WORKDIR}/IO-Compress-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
