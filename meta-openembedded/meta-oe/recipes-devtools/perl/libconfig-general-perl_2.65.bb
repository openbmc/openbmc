DESCRIPTION = "Config file parser module"
HOMEPAGE = "http://search.cpan.org/dist/Config-General/"
LICENSE = "Artistic-2.0 | GPL-1.0-or-later"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://README;beginline=90;endline=90;md5=53fe13727e61798809ec5c160dc93e6e"

SRCNAME = "Config-General"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/T/TL/TLINDEN/${SRCNAME}-${PV}.tar.gz"

SRC_URI[sha256sum] = "4d6d5754be3a9f30906836f0cc10e554c8832e14e7a1341efb15b05d706fc58f"

S = "${WORKDIR}/${SRCNAME}-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}
