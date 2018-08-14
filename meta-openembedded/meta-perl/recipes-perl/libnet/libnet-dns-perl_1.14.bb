DESCRIPTION = "This package contains the DNS.pm module with friends."
HOMEPAGE = "http://www.net-dns.org/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;md5=92d93d8c5bf22de77578531e283dd219"

DEPENDS += "perl"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/N/NL/NLNETLABS/Net-DNS-${PV}.tar.gz"

SRC_URI[md5sum] = "0da1099c0a3548d36ea9e31d5bb9e122"
SRC_URI[sha256sum] = "83c38a594eeb2c85d66e60047a0f5b403f34bd92a5d13606f02e828d450299fc"

S = "${WORKDIR}/Net-DNS-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

RDEPENDS_${PN} = "perl-module-integer"

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}
BBCLASSEXTEND = "native"
