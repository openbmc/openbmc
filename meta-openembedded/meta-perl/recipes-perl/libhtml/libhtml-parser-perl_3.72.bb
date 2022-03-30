DESCRIPTION = "This package contains the Parser.pm module with friends."
HOMEPAGE = "https://metacpan.org/release/HTML-Parser"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;md5=b0459e4426b94753b9a9b8a15f1223b8"

DEPENDS += "perl"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GA/GAAS/HTML-Parser-${PV}.tar.gz"

SRC_URI[md5sum] = "eb7505e5f626913350df9dd4a03d54a8"
SRC_URI[sha256sum] = "ec28c7e1d9e67c45eca197077f7cdc41ead1bb4c538c7f02a3296a4bb92f608b"

S = "${WORKDIR}/HTML-Parser-${PV}"

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
