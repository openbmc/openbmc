DESCRIPTION = "This package contains the DNS.pm module with friends."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;md5=10b1fae0c40a1627bdf0b2a7ac431632"

DEPENDS += "perl"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/N/NL/NLNETLABS/Net-DNS-${PV}.tar.gz"

SRC_URI[md5sum] = "ed17abd6e7e3ba0a8db42649e34a53ae"
SRC_URI[sha256sum] = "a3587b780ca36a8255180ac723d4f6e11407504b5b9a18e0ec098a11c218a51e"

S = "${WORKDIR}/Net-DNS-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}
BBCLASSEXTEND = "native"
