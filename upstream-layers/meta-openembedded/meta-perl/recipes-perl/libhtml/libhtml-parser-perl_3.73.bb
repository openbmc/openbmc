DESCRIPTION = "This package contains the Parser.pm module with friends."
HOMEPAGE = "https://metacpan.org/release/CAPOEIRAB/HTML-Parser-3.73"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;md5=b0459e4426b94753b9a9b8a15f1223b8"

DEPENDS += "perl"

SRC_URI = "${CPAN_MIRROR}/authors/id/C/CA/CAPOEIRAB/HTML-Parser-${PV}.tar.gz"

SRC_URI[sha256sum] = "85f05d944e166b8c5913db54779802eb38f4bbefb89a1730401ef06eb9ad8e69"

S = "${UNPACKDIR}/HTML-Parser-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan ptest-perl

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}

RDEPENDS:${PN} += "\
    perl-module-exporter \
    perl-module-strict \
    perl-module-vars \
    perl-module-xsloader \
    libhtml-tagset-perl \
"

RDEPENDS:${PN}-ptest += "\
    liburi-perl \
    perl-module-config \
    perl-module-file-spec \
    perl-module-filehandle \
    perl-module-io-file \
    perl-module-selectsaver \
    perl-module-test \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
