SUMMARY = "Perl interface to the zlib compression library."
DESCRIPTION = "The Compress::Raw::Zlib module provides a Perl interface \
to the zlib compression library (see 'AUTHOR' for details about where to \
get zlib)."
HOMEPAGE = "https://metacpan.org/release/Compress-Raw-Zlib"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=17;md5=1bb3479faca6bb2086c05a33a934f62a"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/Compress-Raw-Zlib-${PV}.tar.gz"

SRC_URI[sha256sum] = "6d9de0c11921fd520dfd99a3f6b0ca9f1fd9850274f8bec10bbaa4f6803cc049"

DEPENDS += "zlib"

S = "${WORKDIR}/Compress-Raw-Zlib-${PV}"

inherit cpan

export BUILD_ZLIB="0"

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
