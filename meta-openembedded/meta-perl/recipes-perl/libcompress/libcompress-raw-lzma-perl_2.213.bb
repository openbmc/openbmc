SUMMARY = "Low-Level Interface to lzma compresion library."
DESCRIPTION = "This module provides a Perl interface to allow reading and \
wrting of lzma, lzip and xz files/buffers."
HOMEPAGE = "https://metacpan.org/release/Compress-Raw-Lzma"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=2a0fd6d30eaf88289587b776f74c2886"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/Compress-Raw-Lzma-${PV}.tar.gz"

SRC_URI[sha256sum] = "6b979a8347701ced3ac26123d428c1f202b7999850444da19c4aedbb7c862cb7"

DEPENDS += "xz"

S = "${WORKDIR}/Compress-Raw-Lzma-${PV}"

inherit cpan

RDEPENDS:${PN} += "\
    perl-module-universal \
"

export LIBLZMA_INCLUDE="-I${STAGING_DIR_HOST}${includedir}"
export LIBLZMA_LIB="-I${STAGING_DIR_HOST}${libdir}"

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
