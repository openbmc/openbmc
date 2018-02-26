DESCRIPTION = "This package contains the DNS.pm module with friends."
HOMEPAGE = "http://www.net-dns.org/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;md5=92d93d8c5bf22de77578531e283dd219"

DEPENDS += "perl"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/N/NL/NLNETLABS/Net-DNS-${PV}.tar.gz"

SRC_URI[md5sum] = "4fa45b0dfe79667453c1553e16e52c2a"
SRC_URI[sha256sum] = "ebe53e7f433138fb9a7845b4edae6b8f4dc227da97eaf6b347584816ec63a525"

S = "${WORKDIR}/Net-DNS-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}
BBCLASSEXTEND = "native"
