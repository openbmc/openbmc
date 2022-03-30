DESCRIPTION = "The *Compress::Raw::Zlib* module provides a Perl interface to the *zlib* \
compression library (see "AUTHOR" for details about where to get *zlib*)."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

MAINTAINER=	"Poky <poky@yoctoproject.org>"
HOMEPAGE=	"https://metacpan.org/release/Compress-Raw-Zlib"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d"

SRC_URI = "https://cpan.metacpan.org/authors/id/P/PM/PMQS/Compress-Raw-Zlib-2.096.tar.gz"

SRC_URI[md5sum] = "4144ecdd901231553447998cf8035e4a"
SRC_URI[sha256sum] = "cd4cba20c159a7748b8bc91278524a7da70573d9531fde62298609a5f1c65912"

DEPENDS += "zlib"

S = "${WORKDIR}/Compress-Raw-Zlib-${PV}"

inherit cpan

export BUILD_ZLIB="0"

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
