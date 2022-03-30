DESCRIPTION = ""Compress::Raw::Bzip2" provides an interface to the in-memory \
compression/uncompression functions from the bzip2 compression library."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

MAINTAINER=	"Poky <poky@yoctoproject.org>"
HOMEPAGE=	"https://metacpan.org/release/Compress-Raw-Bzip2"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d"

SRC_URI = "https://cpan.metacpan.org/authors/id/P/PM/PMQS/Compress-Raw-Bzip2-2.096.tar.gz"

SRC_URI[md5sum] = "4fdb7ea5071e4b774c52c37331386355"
SRC_URI[sha256sum] = "a564e7634eca7740c5487d01effe1461e9e51b8909e69b3d8f5be98997958cbe"

DEPENDS += "bzip2"

S = "${WORKDIR}/Compress-Raw-Bzip2-${PV}"

inherit cpan

export BUILD_BZIP2="0"
export BZIP2_INCLUDE="-I${STAGING_DIR_HOST}${includedir}"

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
