SUMMARY = "Perl module to manipulate and access gzip files"
DESCRIPTION = "This package contains the gzip.pm module with friends. \
The module implements perlio layer for gzip."

HOMEPAGE = "https://metacpan.org/pod/PerlIO::gzip"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=55;endline=61;md5=bc3da2dec1fbea59ac91172c5e0eb837"

DEPENDS += "perl"

SRC_URI = "https://cpan.metacpan.org/authors/id/N/NW/NWCLARK/PerlIO-gzip-${PV}.tar.gz"

SRC_URI[sha256sum] = "4848679a3f201e3f3b0c5f6f9526e602af52923ffa471a2a3657db786bd3bdc5"

S = "${WORKDIR}/PerlIO-gzip-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

RDEPENDS:${PN} += "perl perl-module-perlio"

BBCLASSEXTEND = "native nativesdk"
