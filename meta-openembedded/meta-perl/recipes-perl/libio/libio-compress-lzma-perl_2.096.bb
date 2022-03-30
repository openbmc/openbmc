DESCRIPTION = "This module provides a Perl interface that allows writing lzma compressed \
data to files or buffer."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

MAINTAINER=	"Poky <poky@yoctoproject.org>"
HOMEPAGE=	"https://metacpan.org/release/IO-Compress-Lzma"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d"

SRC_URI = "https://cpan.metacpan.org/authors/id/P/PM/PMQS/IO-Compress-Lzma-2.096.tar.gz"

SRC_URI[md5sum] = "6c1b70740605b8073e4fbb5ba1e7bbdb"
SRC_URI[sha256sum] = "2f29125f19bb41d29c4b5a2467e3560b7bce5d428176a046b7c8a51609dce6e8"
RDEPENDS:${PN} += "compress-raw-lzma-perl"
RDEPENDS:${PN} += "libio-compress-perl"

S = "${WORKDIR}/IO-Compress-Lzma-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
