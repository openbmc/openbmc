DESCRIPTION = "Config file parser module"
HOMEPAGE = "http://search.cpan.org/dist/Config-General/"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://README;beginline=90;endline=90;md5=3ba4bbac1e79a08332688196f637d2b2"

SRCNAME = "Config-General"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/T/TL/TLINDEN/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "e3ea2a6dc76931cf638b5227aceabf60"
SRC_URI[sha256sum] = "0a9bf977b8aabe76343e88095d2296c8a422410fd2a05a1901f2b20e2e1f6fad"

S = "${WORKDIR}/${SRCNAME}-${PV}"

COMPATIBLE_HOST:libc-musl = "null"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}
