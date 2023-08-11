SUMMARY = "Low-Level Interface to bzip2 compression library"
DESCRIPTION = ""Compress::Raw::Bzip2" provides an interface to the in-memory \
compression/uncompression functions from the bzip2 compression library."
HOMEPAGE = "https://metacpan.org/release/Compress-Raw-Bzip2"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=85ab0f65a47c4c0f72dd6d033ff74ece"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/Compress-Raw-Bzip2-${PV}.tar.gz"

SRC_URI[sha256sum] = "212b81db1c0ae822d19928619a603bd108cb5d5c4700fc67dc7cb169e0cc6525"

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
