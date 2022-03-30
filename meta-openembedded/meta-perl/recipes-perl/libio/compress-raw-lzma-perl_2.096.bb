DESCRIPTION = ""Compress::Raw::Lzma" provides an interface to the in-memory \
compression/uncompression functions from the lzma compression library."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

MAINTAINER=	"Poky <poky@yoctoproject.org>"
HOMEPAGE=	"https://metacpan.org/release/Compress-Raw-Lzma"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d"

SRC_URI = "https://cpan.metacpan.org/authors/id/P/PM/PMQS/Compress-Raw-Lzma-2.096.tar.gz"

SRC_URI[md5sum] = "b5079bb43712fcd1e74b80777fa376ed"
SRC_URI[sha256sum] = "f3afb267b1303b0f125976e9e4a70c6a4a205e35e7c99b408911f5e5c6578217"

DEPENDS += "xz"

S = "${WORKDIR}/Compress-Raw-Lzma-${PV}"

inherit cpan

export LIBLZMA_INCLUDE="-I${STAGING_DIR_HOST}${includedir}"
export LIBLZMA_LIB="-I${STAGING_DIR_HOST}${libdir}"

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
