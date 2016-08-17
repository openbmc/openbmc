DESCRIPTION = "This package contains the DNS.pm module with friends."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;md5=524da96a3365f2caff73fea0ae67c3a0"

DEPENDS += "perl"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/N/NL/NLNETLABS/Net-DNS-${PV}.tar.gz"

SRC_URI[md5sum] = "26375d4310beb108b0e2b3bf30403ee5"
SRC_URI[sha256sum] = "b36c8ead6edf68da5d9de2b0a22a47d7216e2d7eb52c8cde96724988f68a6d46"

S = "${WORKDIR}/Net-DNS-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}
BBCLASSEXTEND = "native"
