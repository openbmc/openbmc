SUMMARY = "Perl interface to the zlib compression library."
DESCRIPTION = "The Compress::Raw::Zlib module provides a Perl interface \
to the zlib compression library (see 'AUTHOR' for details about where to \
get zlib)."
HOMEPAGE = "https://metacpan.org/release/Compress-Raw-Zlib"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=17;md5=000093d18bfdd0ac3594d80b6e055cec"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/Compress-Raw-Zlib-${PV}.tar.gz"

SRC_URI[sha256sum] = "48309aa25f126365a3c14f49b4e90da56d29d3c00506c7e7c3b724f6df0684c8"

DEPENDS += "zlib"

S = "${UNPACKDIR}/Compress-Raw-Zlib-${PV}"

inherit cpan

export BUILD_ZLIB = "0"

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
